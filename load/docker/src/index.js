var browserLoad = require("./browser-load/index.js");
var approvalLoad = require("./approval-load/index.js");
var policyLoad = require("./policy-load/index.js");

var enableBrowserLoad = process.env.ENABLE_BROWSER_LOAD || "0";
var enableApprovalLoad = process.env.ENABLE_APPROVAL_LOAD || "0";
var enablePolicyLoad = process.env.ENABLE_POLICY_LOAD || "0";

var main = function() {
    
    if (enableBrowserLoad == "1") {
        browserLoad.main()
    }
    else if (enableApprovalLoad == "1") {
        approvalLoad.main()
    }
    else if (enablePolicyLoad == "1") {
        policyLoad.main()
    }
}

main();
