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

@Path("/stockInfoServices")
public class StockInfoServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(StockInfoServices.class);

    public StockInfoServices() {
        restClient = new RestClient();
    }

    @GET
    @Path("/getStockInfo/{stockCode}")
    @Produces("text/plain")
    public String getStockInfo(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("stockCode") String stockCode) {

        try {
            String accountId = "456";
            processCustomerRequest(speed, traffic, 2);
            // queryDB("StockDB", "Select * From Stock Where ID = ?", speed, traffic);

            Random rand = new Random();
            double tradePrice = Math.round(rand.nextDouble()*100 * 100.0)/100.0;

            return "{'stockCode':" + stockCode + ", 'message':'Hello!', 'stockPrice':" + tradePrice +"}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }

}