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
import com.appdynamics.sample.service.MongoUtils;
import org.bson.Document;

@Path("/loanServices")
public class LoanServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(LoanServices.class);
    MongoUtils mongoUtils = null;

    public LoanServices() {
        restClient = new RestClient();
        mongoUtils = new MongoUtils("mongodb://mongo-loans:27017");
    }

    @POST
    @Path("/loanApplication")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String loanApplication(String loanInfo, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            Document doc = Document.parse(loanInfo);
            doc.put("requestDate", new Date());
            
            int nextStepDelay = doc.getInteger("step2Delay", 60); // delay is in seconds
            String loanId = doc.getString("loanId");
            
            mongoUtils.insertLoanInfo(loanId, doc, nextStepDelay);
            return "Hello from loanApplication - POST";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApplication: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanVerifyDocumentation")
    @Produces("text/plain")
    public String loanVerifyDocumentation(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            Document filter = new Document("verifyDocumentationDate", new Document("$exists", false));
            filter.append("nextStepAfter", new Document("$lt", new Date()));

            Document doc = mongoUtils.getRandomDocument(filter);

            if (doc != null) {

                int nextStepDelay = doc.getInteger("step3Delay", 60); // delay is in seconds
                String loanId = doc.getString("loanId");
                mongoUtils.markVerifyDocumentationDate(loanId, nextStepDelay);
                return "loanVerifyDocumentation loanId: " + loanId;
            }
            else {
                return "loanVerifyDocumentation, no loanId";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanVerifyDocumentation: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanCreditCheck")
    @Produces("text/plain")
    public String loanCreditCheck(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);

            String federatedHost = System.getenv("FEDERATED_HOST");
            if (federatedHost == null || federatedHost.isEmpty()) {
                makeWebRequest("remote-services", "8080", "rest/remoteServices/loanCreditCheck", speed, traffic);
            }
            else {
                makeWebRequest(System.getenv("FEDERATED_HOST"), System.getenv("FEDERATED_PORT"), "rest/webAPI/checkCredit", speed, traffic);
            }

            Document filter = new Document("creditCheckDate", new Document("$exists", false));
            filter.append("verifyDocumentationDate", new Document("$exists", true));
            filter.append("nextStepAfter", new Document("$lt", new Date()));

            Document doc = mongoUtils.getRandomDocument(filter);

            if (doc != null) {

                int nextStepDelay = doc.getInteger("step4Delay", 60); // delay is in seconds
                String loanId = doc.getString("loanId");
                String creditCheckProvider = doc.getString("creditCheckProvider");
                boolean creditCheckPass = doc.getBoolean("creditCheckPass", true);

                mongoUtils.markCreditCheckDate(loanId, nextStepDelay, creditCheckPass, creditCheckProvider);
                return "loanCreditCheck loanId: " + loanId;
            }
            else {
                return "loanCreditCheck, no loanId";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanCreditCheck: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanUnderwritingComplete")
    @Produces("text/plain")
    public String loanUnderwritingComplete(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);

            Document filter = new Document("loanUnderwritingCompleteDate", new Document("$exists", false));
            filter.append("creditCheckDate", new Document("$exists", true));
            filter.append("nextStepAfter", new Document("$lt", new Date()));

            Document doc = mongoUtils.getRandomDocument(filter);

            if (doc != null) {

                int nextStepDelay = doc.getInteger("step5Delay", 60); // delay is in seconds
                String loanId = doc.getString("loanId");
                int loanAmount = doc.getInteger("loanAmount", 0);

                mongoUtils.markLoanUnderwritingCompleteDate(loanId, nextStepDelay);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");     
                String content = formatter.format(new Date()) + " INFO loanId:" + loanId + " loanAmount:" + loanAmount + " approved:true\n";
                writeToLogFile(content);

                return "loanUnderwritingComplete loanId: " + loanId;
            }
            else {
                return "loanUnderwritingComplete, no loanId";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanUnderwritingComplete: " + e.getMessage();
        }
    }

    private void writeToLogFile(String content) {

        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        String fileName = "//loanLogs//loanUnderwriting.log";

        try {
            
            fileWriter = new FileWriter(fileName, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);

            System.out.println("Done");

        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @GET
    @Path("/loanApproved")
    @Produces("text/plain")
    public String loanApproved(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);

            Document filter = new Document("loanApprovedDate", new Document("$exists", false));
            filter.append("loanUnderwritingCompleteDate", new Document("$exists", true));
            filter.append("nextStepAfter", new Document("$lt", new Date()));

            Document doc = mongoUtils.getRandomDocument(filter);

            if (doc != null) {

                String loanId = doc.getString("loanId");
                mongoUtils.markLoanApprovedDate(loanId);
                return "loanApproved loanId: " + loanId;
            }
            else {
                return "loanApproved, no loanId";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApproved: " + e.getMessage();
        }
    }
}
