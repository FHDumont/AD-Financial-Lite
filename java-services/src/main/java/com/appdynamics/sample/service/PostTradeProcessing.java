package com.appdynamics.sample.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.net.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.appdynamics.sample.service.JMSListener;
import com.appdynamics.sample.service.JMSListenerCallback;

@Path("/postTradeProcessing")
public class PostTradeProcessing extends BaseService implements JMSListenerCallback
{
    RestClient restClient = null;
    Boolean isInitialized = false;
    Logger logger = Logger.getLogger(PostTradeProcessing.class);

    public PostTradeProcessing() {
        restClient = new RestClient();
    }

    public void messageReceived(String speed, String traffic) {

        try {
            String quoteID = "123";
            processCustomerRequest("", "");
            makeWebRequest("settlementServices", "8080", "rest/settlementServices/settleTradeAccounts/" + quoteID, speed, traffic);

            Random rand = new Random();
            int n = rand.nextInt(10);
            if (n > 7) {
                callS3("postTradeShare");
            }
            if (n > 4) {
                queryDynamoDB("PostTrade", "postTradeId");
            } 
        }
        catch (Exception e) {
            e.printStackTrace();
            // return "Error from login: " + e.getMessage();
        }
    }

    @GET
    @Path("/init")
    @Produces("text/plain")
    public String init() {

        if (isInitialized == false) {
            try {
                JMSListener jmsListener = new JMSListener("PostTradeQueue", this);
                jmsListener.run();
                isInitialized = true;
                return "PostTradeProcessing.init() complete";
            }
            catch (Exception e) {
                e.printStackTrace();
                return "Error from init: " + e.getMessage();
            }        
        }
        else {
            return "PostTradeProcessing is already initialized";
        }
    }
    // @GET
    // @Path("/addTradeData/{quoteID}")
    // @Produces("text/plain")
    // public String addTradeData(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("quoteID") String quoteID) {


    // } 
}