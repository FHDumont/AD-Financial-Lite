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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import com.appdynamics.sample.rest.client.RestClient;
import org.apache.log4j.Logger;
import java.util.Random;
import com.appdynamics.sample.service.BaseService;

@Path("/webFrontEnd")
public class WebFrontEnd extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(WebFrontEnd.class);

    public WebFrontEnd() {
        restClient = new RestClient();
    }

    @GET
    @Path("/")
    @Produces("text/plain")
    public String homePage() {

        try {
            return "Hello from homePage";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from homePage: " + e.getMessage();
        }
    }

    @GET
    @Path("/login")
    @Produces("text/plain")
    public String login(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("authenticationServices", "8005", "authenticate", speed, traffic);
            return "Hello from login";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from login: " + e.getMessage();
        }
    }

    @GET
    @Path("/accountsHome")
    @Produces("text/plain")
    public String accountsHome(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("accountManagement", "8080", "rest/accountManagement/accountsHome", speed, traffic);
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
            postWebRequest("accountManagement", "8080", "rest/accountManagement/loanApplication", customerInfo, speed, traffic);

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
    public String loanVerifyDocumentation(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("accountManagement", "8080", "rest/accountManagement/loanVerifyDocumentation", speed, traffic);
            return "Hello from loanVerifyDocumentation";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanVerifyDocumentation: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanUnderwritingComplete")
    @Produces("text/plain")
    public String loanUnderwritingComplete(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("accountManagement", "8080", "rest/accountManagement/loanUnderwritingComplete", speed, traffic);
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
    public String loanApproved(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("accountManagement", "8080", "rest/accountManagement/loanApproved", speed, traffic);
            return "Hello from loanApproved";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApproved: " + e.getMessage();
        }
    }

    @GET
    @Path("/researchStock")
    @Produces("text/plain")
    public String researchStock(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("quoteServices", "8080", "rest/quoteServices/researchStock/"+ getRandomStockCode(), speed, traffic);
            return "Hello from researchStock";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from researchStock: " + e.getMessage();
        }
    }

    // @GET
    // @Path("/getQuote")
    // @Produces("text/plain")
    // public String getQuote(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

    //     try {
    //         processCustomerRequest(speed, traffic);
    //         makeWebRequest("quoteServices", "8080", "rest/quoteServices/getStockQuote/ABC", speed, traffic);
    //         return "Hello from getQuote";
    //     }
    //     catch (Exception e) {
    //         e.printStackTrace();
    //         return "Error from getQuote: " + e.getMessage();
    //     }
    // }

    @GET
    @Path("/getQuote")
    @Produces("text/plain")
    public String getQuote(@Context UriInfo uri_info) {
        try {
            MultivaluedMap<String,String> params = uri_info.getQueryParameters();
            String speed = params.getFirst("speed");
            String traffic = params.getFirst("traffic");

            processCustomerRequest(speed, traffic);
            makeWebRequest("quoteServices", "8080", "rest/quoteServices/getStockQuote/"+ getRandomStockCode(), params);
            return "Hello from getQuote";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error from getQuote: " + e.getMessage();
        }
    }

    @GET
    @Path("/processTrade")
    @Produces("text/plain")
    public String processTrade(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            String quoteID = "12345";

            makeWebRequest("orderProcessing", "8080", "rest/orderProcessing/processTrade/" + quoteID, speed, traffic);
            return "Hello from processTrade";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from processTrade: " + e.getMessage();
        }
    }

    @GET
    @Path("/researchStock-cloud")
    @Produces("text/plain")
    public String researchStockCloud(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest("", "");
            makeWebRequest("quoteServices-cloud", "8080", "rest/quoteServices/researchStock-cloud/"+ getRandomStockCode(), speed, traffic);
            return "Hello from researchStock";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from researchStock: " + e.getMessage();
        }
    }
        
    @GET
    @Path("/getQuote-cloud")
    @Produces("text/plain")
    public String getQuoteCloud(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest("", "");
            makeWebRequest("quoteServices-cloud", "8080", "rest/quoteServices/getStockQuote-cloud/"+ getRandomStockCode(), speed, traffic);
            return "Hello from getQuote";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from getQuote: " + e.getMessage();
        }
    }

    @GET
    @Path("/processTrade-cloud")
    @Produces("text/plain")
    public String processTradeCloud(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest("", "");
            String quoteID = "12345";

            makeWebRequest("orderProcessing-cloud", "8080", "rest/orderProcessing/processTrade-cloud/" + quoteID, speed, traffic);
            return "Hello from processTrade";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from processTrade: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyApplication")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyApplication(String policyData, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("accountManagement", "8080", "rest/accountManagement/policyApplication", policyData, speed, traffic);
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
    public String policyVehicleEntry(String policyData, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("accountManagement", "8080", "rest/accountManagement/policyVehicleEntry", policyData, speed, traffic);
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
    public Response policyDriverEntry(String policyData, @QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            return postWebRequest("accountManagement", "8080", "rest/accountManagement/policyDriverEntry", policyData, speed, traffic);
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
    public String policyFetchDiscounts(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("accountManagement", "8080", "rest/accountManagement/policyFetchDiscounts", policyData, speed, traffic);
            return "Hello from policyFetchDiscounts";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyFetchDiscounts: " + e.getMessage();
        }
    }

    @POST
    @Path("/policyGenerateQuote")
    @Produces("text/plain")
    @Consumes ("application/json")
    public String policyGenerateQuote(String policyData, @QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            postWebRequest("accountManagement", "8080", "rest/accountManagement/policyGenerateQuote", policyData, speed, traffic);
            return "Hello from policyGenerateQuote";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from policyGenerateQuote: " + e.getMessage();
        }
    }

    private String getRandomStockCode() {
        String[] stockCodes = {"ABC", "DEF", "GHI", "JKL", "MNO", "PQR", "STU", "VWX", "YZ"};
        Random rand = new Random(); 
        return stockCodes[rand.nextInt(stockCodes.length)];
    }
}