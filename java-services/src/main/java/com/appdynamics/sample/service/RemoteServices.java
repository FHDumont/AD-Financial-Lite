package com.appdynamics.sample.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

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

@Path("/remoteServices")
public class RemoteServices extends BaseService
{
    RestClient restClient = null;
    MongoUtils mongoUtils = null;
    Logger logger = Logger.getLogger(RemoteServices.class);

    public RemoteServices() {
        restClient = new RestClient();
        mongoUtils = new MongoUtils("mongodb://mongo-loans:27017");
    }

    @GET
    @Path("/loanCreditCheck")
    @Produces("text/plain")
    public String loanCreditCheck(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            return "Hello from loanCreditCheck";
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

            Document filter = new Document("loanUnderwritingCompleteDate", new Document("$exists", false));
            filter.append("creditCheckDate", new Document("$exists", true));
            filter.append("nextStepAfter", new Document("$lt", new Date()));

            Document doc = mongoUtils.getRandomDocument(filter);

            if (doc != null) {

                int nextStepDelay = doc.getInteger("step5Delay", 60); // delay is in seconds
                String loanId = doc.getString("loanId");
                int loanAmount = doc.getInteger("loanAmount", 0);

                String postData = "[{\"loanId\":\"" + loanId + "\",\"loanAmount\":" + loanAmount + ",\"approved\":true}]";
                postCustomEvent(postData);

                mongoUtils.markLoanUnderwritingCompleteDate(loanId, nextStepDelay);

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

    private void postCustomEvent(String postData) {

        try {
            String url = "http://soak44.api.appdynamics.com/events/publish/loanUnderwriting";
            byte[] postDataBytes = postData.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postDataBytes.length;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setDoOutput( true );
            con.setInstanceFollowRedirects( false );
            con.setRequestMethod( "POST" );
            con.setRequestProperty( "X-Events-API-AccountName", "customer1_e386a2c8-5c73-491f-8a01-0825b8e48032"); 
            con.setRequestProperty( "X-Events-API-Key", "ef51e061-ed1e-4862-ba9e-58b04fc2fe3e"); 
            con.setRequestProperty( "Content-Type", "application/vnd.appd.events+json;v=2"); 
            con.setRequestProperty( "Accept", "application/json");
            con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            con.setUseCaches( false );
            con.getOutputStream().write(postDataBytes);
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error from postCustomEvent: " + e.getMessage());
        }
    }
}
