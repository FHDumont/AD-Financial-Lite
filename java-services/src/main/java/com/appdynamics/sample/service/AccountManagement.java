package com.appdynamics.sample.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.net.*;

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

@Path("/accountManagement")
public class AccountManagement extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(AccountManagement.class);

    public AccountManagement() {
        restClient = new RestClient();
    }

    @GET
    @Path("/accountsHome")
    @Produces("text/plain")
    public String accountsHome(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("sessionTracking", "8003", "logSession", speed, traffic);
            makeWebRequest("balanceServices", "8080", "rest/balanceServices/getAccountsBalances/124", speed, traffic);
            return "Hello from accountsHome";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from accountsHome: " + e.getMessage();
        }
    }

    @POST
    @Path("/loanApplication")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String loanApplication(String customerInfo, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("loanServices", "8080", "rest/loanServices/loanApplication", customerInfo, speed, traffic);
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
            makeWebRequest("loanServices", "8080", "rest/loanServices/loanVerifyDocumentation", speed, traffic);
            return "Hello from loanVerifyDocumentation";
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
            makeWebRequest("loanServices", "8080", "rest/loanServices/loanCreditCheck", speed, traffic);
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
            processCustomerRequest(speed, traffic);
            makeWebRequest("loanServices", "8080", "rest/loanServices/loanUnderwritingComplete", speed, traffic);
            return "Hello from loanUnderwritingComplete";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanUnderwritingComplete: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanApproved")
    @Produces("text/plain")
    public String loanApproved(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("loanServices", "8080", "rest/loanServices/loanApproved", speed, traffic);
            return "Hello from loanApproved";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApproved: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyApplication")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyApplication(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("policyServices", "8080", "rest/policyServices/policyApplication", policyData, speed, traffic);
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
    public String policyVehicleEntry(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("policyServices", "8080", "rest/policyServices/policyVehicleEntry", policyData, speed, traffic);
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
    public Response policyDriverEntry(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            return postWebRequest("policyServices", "8080", "rest/policyServices/policyDriverEntry", policyData, speed, traffic);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/policyFetchDiscounts")
    @Produces("text/plain")
    @Consumes ("application/json")
    public Response policyFetchDiscounts(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            return postWebRequest("policyServices", "8080", "rest/policyServices/policyFetchDiscounts", policyData, speed, traffic);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/policyGenerateQuote")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyGenerateQuote(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("policyServices", "8080", "rest/policyServices/policyGenerateQuote", policyData, speed, traffic);
            return "Hello from policyGenerateQuote";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyGenerateQuote: " + e.getMessage();
        }
    }

}