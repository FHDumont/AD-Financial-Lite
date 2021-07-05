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

@Path("/wireServices")
public class WireServices extends BaseService
{
    RestClient restClient = null;
    
    Logger logger = Logger.getLogger(WireServices.class);

    public WireServices() {
        restClient = new RestClient();
    }

    @GET
    @Path("/loanCreditCheck")
    @Produces("text/plain")
    public String loanCreditCheck(@QueryParam("speed") String speed,@QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("accountManagement", "8080", "rest/accountManagement/loanCreditCheck", speed, traffic);
            return "Hello from loanCreditCheck";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanCreditCheck: " + e.getMessage();
        }
    }

    @GET
    @Path("/loanApproved")
    @Produces("text/plain")
    public String loanApproved(@QueryParam("speed") String speed, @QueryParam("traffic") String traffic) {

        try {
            processCustomerRequest(speed, traffic);
            makeWebRequest("balanceServices", "8080", "rest/balanceServices/loanApproved", speed, traffic);
            return "message:Hello!";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error from loanApproved: " + e.getMessage();
        }
    }
}