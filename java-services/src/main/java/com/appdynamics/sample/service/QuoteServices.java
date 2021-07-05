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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.appdynamics.sample.rest.client.RestClient;
import org.apache.log4j.Logger;
import java.util.Random;
import com.appdynamics.sample.service.BaseService;

@Path("/quoteServices")
public class QuoteServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(QuoteServices.class);

    public QuoteServices() {
        restClient = new RestClient();
    }

    @GET
    @Path("/researchStock/{stockCode}")
    @Produces("text/plain")
    public String researchStock(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

        try {
            processCustomerRequest(speed, traffic, 2);
            queryDB("StockDB", "Select * From Stock", speed, traffic);
            return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }

    // @GET
    // @Path("/getStockQuote/{stockCode}")
    // @Produces("text/plain")
    // public String getStockQuote(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

    //     try {
    //         String accountId = "456";
    //         processCustomerRequest(speed, traffic, 2);
    //         queryDB("StockDB", "Select * From Stock Where ID = ?", speed, traffic);
    //         makeWebRequest("stockInfoServices", "8080", "rest/stockInfoServices/getStockInfo/" + stockCode, speed, traffic);

    //         return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
    //     }
    //     catch (Exception e) {
    //         e.printStackTrace();
    //         return "Error from login: " + e.getMessage();
    //     }
    // }

    @GET
    @Path("/getStockQuote/{stockCode}")
    @Produces("text/plain")
    public String getStockQuote(@Context UriInfo uri_info) {
        try {
            MultivaluedMap<String,String> queryparams = uri_info.getQueryParameters();
            MultivaluedMap<String,String> pathParams = uri_info.getPathParameters();
            String speed = queryparams.getFirst("speed");
            String traffic = queryparams.getFirst("traffic");
            String stockCode = pathParams.getFirst("stockCode");

            processCustomerRequest(speed, traffic, 2);
            queryDB("StockDB", "Select * From Stock Where ID = ?", speed, traffic);
            makeWebRequest("stockInfoServices", "8080", "rest/stockInfoServices/getStockInfo/" + stockCode, queryparams);

            return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error from getStockQuote: " + e.getMessage();
        }
    }

    @GET
    @Path("/researchStock-cloud/{stockCode}")
    @Produces("text/plain")
    public String researchStockCloud(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

        try {
            processCustomerRequest("", "");
            
            Random rand = new Random();
            int n = rand.nextInt(10);
            if (n > 4) {
                queryDynamoDB("Quotes", "quoteId");
            } 

            makeWebRequest("riskProcessing", "8080", "rest/riskProcessing/getStockRisk/" + stockCode, speed, traffic);
 
            return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }

    @GET
    @Path("/getStockQuote-cloud/{stockCode}")
    @Produces("text/plain")
    public String getStockQuoteCloud(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

        try {
            String accountId = "456";
            processCustomerRequest("", "");
        
            Random rand = new Random();
            int n = rand.nextInt(10);
            if (n > 4) {
                queryDynamoDB("Quotes", "quoteId");
            } 
            makeWebRequest("pricingServices", "8080", "rest/pricingServices/getStockPrice/" + stockCode, speed, traffic);
        
            return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }

}