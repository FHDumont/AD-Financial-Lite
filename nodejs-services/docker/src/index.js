var winston = require('winston');
var http = require('http')
var path = require('path');
var MongoClient = require('mongodb').MongoClient;
var port = process.env.PORT || 8001
var lodash = require('lodash');
var HttpDispatcher = require('httpdispatcher');
var dispatcher = new HttpDispatcher();
var d = require('domain').create()

var nodeName = process.env.TIER_NAME + "Node";	
var nodeNameSuffix = process.env.NODE_NAME_SUFFIX;	

if (nodeNameSuffix) {	
    nodeName = nodeName + "-" + nodeNameSuffix;	
}

var controllerSslEnabled = false;
if (process.env.APPDYNAMICS_CONTROLLER_SSL_ENABLED && process.env.APPDYNAMICS_CONTROLLER_SSL_ENABLED == "true") {
    controllerSslEnabled = true;
}

var appd = require('appdynamics');
appd.profile({
    controllerHostName: process.env.APPDYNAMICS_CONTROLLER_HOST_NAME,
    controllerPort: process.env.APPDYNAMICS_CONTROLLER_PORT,
    controllerSslEnabled: controllerSslEnabled,
    accountName: process.env.APPDYNAMICS_AGENT_ACCOUNT_NAME,
    accountAccessKey: process.env.APPDYNAMICS_AGENT_ACCOUNT_ACCESS_KEY,
    applicationName: process.env.APPLICATION_NAME,	
    tierName: process.env.TIER_NAME,
    nodeName: nodeName,
    noNodeNameSuffix: true,	 
    analytics: { host: 'machine-agent', port: 9090 },	
    libagent: true,	
    debug: true
});

var lightTrafficMS = 50;
var moderateTrafficMS = 100;
var heavyTrafficMS = 500;
var normalSpeedMS = 100;
var slowSpeedMS = 1000;
var verySlowSpeedMS = 5000;
var lightTrafficErrorRate = 4;
var moderateTrafficErrorRate = 8;
var heavyTrafficErrorRate = 40;

var badNode = process.env.BAD_NODE || false;
var debugConsole = process.env.DEBUG_CONSOLE || false;
var consoleLevel = process.env.CONSOLE_LOG_LEVEL || 'info';
var logFilePath = process.env.LOG_PATH || (path.join(path.dirname(require.main.filename), '../logs/'));
var logFileLevel = process.env.LOG_LEVEL || 'info';
var logFileName = "Log" + '-' + process.pid + '-' + Date.now() + '.log';

callback = function (response) {
    var str = ''
    response.on('data', function (chunk) {
        str += chunk;
    });

    response.on('end', function () {
        console.log(str);
    });
}

function handleRequest(request, response) {
    try {
        console.log(request.url);
        dispatcher.dispatch(request, response);
    }
    catch (err) {
        console.log(err);
    }
}

function getResponseProperties(request) {

    var sleepMS = lightTrafficMS;
    var errorRate = lightTrafficErrorRate;
    var speed = "normal";
    var traffic = "light";

    if (request.query && request.query.speed) {
        speed = request.query.speed;
    }

    if (request.query && request.query.traffic) {
        traffic = request.query.traffic;
    }

    if (traffic == "moderate") {
        sleepMS = moderateTrafficMS;
        errorRate = moderateTrafficErrorRate;
    }
    else if (traffic == "heavy") {
        sleepMS = heavyTrafficMS;
        errorRate = heavyTrafficErrorRate;
    }

    if (speed == "slow") {
        sleepMS += lodash.random(1, slowSpeedMS);
    }
    else if (speed == "veryslow") {
        sleepMS += lodash.random(1, verySlowSpeedMS);
    }
    else {
        sleepMS += lodash.random(1, normalSpeedMS);
    }

    return { "sleepMS": sleepMS, "errorRate": errorRate, "speed": speed, "traffic": traffic };
}

dispatcher.onGet("/", function (request, response) {
    response.writeHead(200, { 'Content-Type': 'text/html' });
    response.end('<h1>Hey, this is the homepage of your server</h1>');
});

dispatcher.onGet("/authenticate", function (request, response) {

    var responseProperties = getResponseProperties(request);

    if (responseProperties.errorRate > lodash.random(0, 100)) {
        response.status = 500;
        response.end("Error in " + request.url);
    }
    else {

        var sessionsOptions = { host: 'sessionTracking', path: '/logSession', port: '8003' };

        var newReq = http.request(sessionsOptions, function (res) {
            var str = ''
            res.on('data', function (chunk) {
                str += chunk;
            });

            res.on('end', function () {
                console.log(str);
                response.statusCode = res.statusCode;
                console.log("Session tracking response");
                console.log(res);
                if (res.statusCode >= 400) {
                    response.end(res.body);
                }
                else {
                    var mongoDBURL = "mongodb://mongo-sessions:27017/sessions";
                    var doc = {
                        sessionId: lodash.random(),
                        sessionDate: new Date().toISOString()
                    };
                    mongoDBInsert(mongoDBURL, "Sessions", doc, responseProperties.sleepMS, function (err) {
                        if (err) {
                            console.log(err);
                            response.writeHead(500, { 'Content-Type': 'text/plain' });
                            response.end(err.errno);
                        }
                        else {
                            console.log("Success");
                            response.writeHead(200, { 'Content-Type': 'text/plain' });
                            response.end('Sessions');
                        }
                    });
                }
            });

            res.on('error', function (err) {
                response.statusCode = 500;
                response.end(err.body);
            });
        });
        newReq.end();
        response.end('<html><body><h1>Hello from authenticate</h1></body></html>');
    }
});

dispatcher.onGet("/logSession", function (request, response) {

    var responseProperties = getResponseProperties(request);

    if (responseProperties.errorRate > lodash.random(0, 100)) {
        response.status = 500;
        response.end("Error in " + request.url);
    }
    else {
        var mongoDBURL = "mongodb://mongo-sessions:27017/sessions";
        var doc = {
            sessionId: lodash.random(),
            sessionDate: new Date().toISOString()
        };
        mongoDBInsert(mongoDBURL, "Sessions", doc, responseProperties.sleepMS, function (err) {

            if (err) {
                response.writeHead(500, { 'Content-Type': 'text/plain' });
                response.end(err.errno);
            }
            else {
                response.writeHead(200, { 'Content-Type': 'text/plain' });
                response.end('Sessions');
            }
        });
        // response.end('<html><body><h1>Hello from logSession</h1></body></html>');
    }
});

dispatcher.onError(function (request, response) {
    response.writeHead(404);
    response.end("Error, the URL doesn't exist");
});

var makeMongoDBCall = function (mongoDBURL, dbName, waitMS, parentCallback) {

    d.on('error', function (err) {
        parentCallback(err);
    })

    d.run(function () {

        var waitTill = new Date(new Date().getTime() + waitMS);
        while (waitTill > new Date()) { }

        var dbRecord = {
            time: (new Date()).getTime(),
            text: "DBText"
        };

        MongoClient.connect(mongoDBURL, function (err, db) {
            if (err) throw err;

            var waitTill2 = new Date(new Date().getTime() + lodash.random(200, 300));
            while (waitTill2 > new Date()) { }

            db.collection(dbName).insertOne(dbRecord, function (err1, res) {

                if (err1) {
                    console.log("Error calling: " + mongoDBURL + ", ");
                    console.log(err);
                }
                db.close();
                logger.log('info', "1 record inserted");
                parentCallback(err1, res);
            });
        });
    });
}

var mongoDBInsert = function (mongoDBURL, dbName, doc, nextStepDelay, parentCallback) {

    var waitTill = new Date(new Date().getTime() + nextStepDelay);
    while (waitTill > new Date()) {

    }

    MongoClient.connect(mongoDBURL, function (err, db) {

        if (err) {
            console.log("mongoDBInsert: err");
            console.log(err);
            parentCallback(err, null);
        }
        else {
            db.collection(dbName).insertOne(doc, function (err1, res) {
                if (err1) {
                    console.log("Error calling: " + mongoDBURL + ", ");
                    console.log(err);
                }
                db.close();
                parentCallback(err1, res);
            });

        }
    });
}

var mongoDBGet = function (mongoDBURL, dbName, filter, parentCallback) {

    MongoClient.connect(mongoDBURL, function (err, db) {

        if (err) {
            console.log("Error calling: " + mongoDBURL + ", ");
            console.log(err);
            throw err;
        }

        db.collection(dbName).find(filter).toArray(function (err1, results) {

            var recordCount = results.length;
            var selectedIndex = lodash.random(0, (recordCount - 1));
            var selectedRecord = results[selectedIndex];

            db.close();
            if (err1) {
                console.log("Error calling: " + mongoDBURL + ", ");
                console.log(err1);
                throw err1;
            }

            parentCallback(err, selectedRecord);
            // console.log(res);
        });
    });
}

var mongoDBUpdate = function (mongoDBURL, dbName, recordId, updateFields, parentCallback) {

    MongoClient.connect(mongoDBURL, function (err, db) {

        if (err) throw err;

        db.collection(dbName).updateMany({ order_id: recordId }, { $set: updateFields }, function (err1, results) {

            db.close();
            if (err1) {
                console.log("mongoDBUpdate: Error calling: " + mongoDBURL + ", ");
                console.log(err1);
                throw err1;
            }

            parentCallback(err1, results);
            // console.log(res);
        });
    });
}

var mongoDBDelete = function (mongoDBURL, dbName, recordId, parentCallback) {

    MongoClient.connect(mongoDBURL, function (err, db) {

        if (err) throw err;

        db.collection(dbName).deleteMany({ order_id: recordId }, function (err, results) {

            db.close();
            if (err) {
                console.log("Error calling: " + mongoDBURL + ", ");
                console.log(err);
                throw err;
            }

            parentCallback(err, results);
            // console.log(res);
        });
    });
}

var logger = new winston.Logger({
    transports: [
        new (winston.transports.File)({
            name: 'file',
            filename: logFilePath + logFileName,
            level: logFileLevel
        }),
        new (winston.transports.Console)({
            name: 'console',
            level: consoleLevel
        })
    ]
});
if (!debugConsole) {
    logger.remove('console');
}

var server = http.createServer(handleRequest)

server.listen(port, (err) => {
    if (err) {
        return logger.log('error', 'something bad happened', err)
    }
    console.log('process.pid: ' + process.pid);
    console.log(`server is listening on ${port}`)
})