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

@Path("/balanceServices")
public class BalanceServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(BalanceServices.class);

    public BalanceServices() {
        restClient = new RestClient();
    }

    @GET
    @Path("/getAccountsBalances/{customerId}")
    @Produces("text/plain")
    public String getAccountsBalances(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("customerId") String customerId) {

        try {
            processCustomerRequest(speed, traffic);
            queryDB("AccountDB", "Select * From Accounts Where CustomerId = ?", speed, traffic);

            return "{'customerId':" + customerId + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from getAccountsBalances: " + e.getMessage();
        }
    }

    @GET
    @Path("/getAccountBalance/{accountId}")
    @Produces("text/plain")
    public String getAccountBalance(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("accountId") String accountId) {

        try {
            processCustomerRequest(speed, traffic);
            queryDB("AccountDB", "Select * From Accounts Where AccountId = ?", speed, traffic);
            return "{'accountId':" + accountId + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from getAccountBalance: " + e.getMessage();
        }
    }  

    @GET
    @Path("/loanApproved/{loanId}")
    @Produces("text/plain")
    public String loanApproved(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic, @PathParam("loanId") String loanId) {

        try {
            processCustomerRequest(speed, traffic);
            queryDB("AccountDB", "Select * From Accounts Where AccountId = ?", speed, traffic);
            return "{'loanId':" + loanId + ", 'message':'Hello!''}";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApproved: " + e.getMessage();
        }
    }  
}