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
import com.appdynamics.sample.service.JMSMessageProducer;

@Path("/orderProcessing")
public class OrderProcessing extends BaseService
{
    RestClient restClient = null;

    Logger logger = Logger.getLogger(OrderProcessing.class);

    public OrderProcessing() {
        restClient = new RestClient();
    }

    @GET
    @Path("/processTrade/{quoteID}")
    @Produces("text/plain")
    public String processTrade(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("quoteID") String quoteID) {

        try {
            processCustomerRequest(speed, traffic, 2);    
            queryDB("OrderDB", "Insert Into Order(Col1,Col2,Col3,Col4)(?,?,?,?)", speed, traffic);

            Random rand = new Random();
            double tradePrice = Math.round(rand.nextDouble()*100*100.0)/100.0;
            int tradeQuantityAmount = rand.nextInt(1234);
            double tradeDollarAmount = tradePrice * tradeQuantityAmount;

            return "{'quoteID':" + quoteID + ", 'message':'Hello!', 'tradeQty':" + tradeQuantityAmount + ", 'tradeAmount':" + tradeDollarAmount + "}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }
    
    @GET
    @Path("/processTrade-cloud/{quoteID}")
    @Produces("text/plain")
    public String processTradeCloud(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("quoteID") String quoteID) {

        try {
            processCustomerRequest("", "");

            Random rand = new Random();
            int n = rand.nextInt(10);
            if (n > 4) {
                queryDynamoDB("Quotes", "quoteId");
            }

            makeWebRequest("riskProcessing", "8080", "rest/riskProcessing/addTradeData/" + quoteID, speed, traffic);
            thread(new JMSMessageProducer("PostTradeQueue", speed, traffic), false);
            thread(new JMSMessageProducer("DataWarehouseQueue", speed, traffic), false);

            return "{'quoteID':" + quoteID + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }    
}