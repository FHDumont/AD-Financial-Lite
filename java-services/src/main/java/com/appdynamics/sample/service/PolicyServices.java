package com.appdynamics.sample.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.net.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.appdynamics.sample.rest.client.RestClient;
import org.apache.log4j.Logger;
import java.util.Random;
import com.appdynamics.sample.service.BaseService;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

@Path("/policyServices")
public class PolicyServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(PolicyServices.class);

    public PolicyServices() {
        restClient = new RestClient();
    }

    @POST
    @Path("/policyApplication")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyApplication(String policyInfo, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(policyInfo);
            doc.put("requestDate", new Date());
            updatePolicyInfo(doc);
            return "Hello from policyApplication - POST";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyApplication: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyVehicleEntry")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyVehicleEntry(String policyInfo, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(policyInfo);
            updatePolicyInfo(doc);
            return "Hello from policyVehicleEntry - POST";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyVehicleEntry: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyDriverEntry")
    @Produces("text/plain")
    @Consumes ("application/json")
    public Response policyDriverEntry(String policyInfo, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {
        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(policyInfo);
            updatePolicyInfo(doc);
            return Response.ok("Hello from policyDriverEntry - POST", MediaType.TEXT_PLAIN).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/policyFetchDiscounts")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyFetchDiscounts(String policyInfo, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {
        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(policyInfo);
            updatePolicyInfo(doc);
            return "Hello from policyFetchDiscounts - POST";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyFetchDiscounts: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyGenerateQuote")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyGenerateQuote(String policyInfo, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(policyInfo);
            updatePolicyInfo(doc);
            return "Hello from policyGenerateQuote - POST";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyGenerateQuote: " + e.getMessage();
        }
    }

    private static String MongoURL = "mongodb://mongo-policies:27017"; 

    private void updatePolicyInfo(Document policyInfo) {

        String policyId = policyInfo.getString("policyId");
        MongoClient mongoClient = new MongoClient(new MongoClientURI(MongoURL));

        if (policyInfo.containsKey("addError") && policyInfo.getBoolean("addError") == true) {
            mongoClient.close();
        }

        // try {
        MongoDatabase database = mongoClient.getDatabase("policies");
        MongoCollection<Document> collection = database.getCollection("policies");
        collection.updateOne(new Document("policyId", policyId), new Document("$set", new Document("lastUpdatedDate", new Date())), (new UpdateOptions()).upsert(true));
        mongoClient.close();
        // }
        // catch (Exception e) {
        //     e.printStackTrace();
        //     return "Error from updatePolicyInfo: " + e.getMessage();
        // }
    }
}
