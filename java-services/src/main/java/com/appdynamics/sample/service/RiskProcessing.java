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

@Path("/riskProcessing")
public class RiskProcessing extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(RiskProcessing.class);

    public RiskProcessing() {
        restClient = new RestClient();
    }

    @GET
    @Path("/getStockRisk/{stockCode}")
    @Produces("text/plain")
    public String getStockRisk(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

        try {
            processCustomerRequest("", "");
            // TODO
            // DB calls
            return "{'stockCode':" + stockCode + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }
    
    @GET
    @Path("/addTradeData/{quoteID}")
    @Produces("text/plain")
    public String addTradeData(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("quoteID") String quoteID) {

        try {
            processCustomerRequest("", "");
            // TODO
            // DB calls
            return "{'quoteID':" + quoteID + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }    
}