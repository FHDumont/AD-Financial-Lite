package com.appdynamics.sample.filter;

import javax.ws.rs.ext.*;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.*;

import com.appdynamics.sample.filter.CPUHog;
import com.appdynamics.sample.filter.MemoryHog;

@Provider
public class PreRequestFilter implements ContainerRequestFilter {
    
    @Override 
    public void filter(ContainerRequestContext ctx) throws IOException{
        System.out.println("-- req info --");
        log(ctx.getUriInfo(), ctx.getUriInfo().getQueryParameters());
        logHeaders(ctx.getUriInfo(), ctx.getHeaders());

        String envName = "";

        if (System.getenv("TIER_NAME") != null) {
            envName = System.getenv("TIER_NAME").trim().toLowerCase();
        }        


        MultivaluedMap<String, String> params = ctx.getUriInfo().getQueryParameters();
        if (params.containsKey("spike") && params.containsKey("tier")) {
            String spike_val = params.getFirst("spike").toString().toLowerCase();
            String resource_tier = params.getFirst("tier").toString().toLowerCase();

            System.out.println("Spike param: " + spike_val);
            System.out.println("Tier param: " + resource_tier);
            System.out.println("Tier name: " + envName);

            System.out.println("Compare: " + resource_tier.compareToIgnoreCase(envName));

            if (resource_tier.compareToIgnoreCase(envName) == 0) {
                if (spike_val.compareToIgnoreCase("cpu") == 0) {
                    System.out.println("Starting CPU hog");
                    new CPUHog().start();
                }
    
                if (spike_val.contains("memory")) {
                    String memory_hog_val = params.getFirst("status").toString().toLowerCase();
                    if (memory_hog_val.compareToIgnoreCase("on") == 0) {
                        try {
                            System.out.println("Starting memory hog");
                            new MemoryHog().bloat();
                        } catch (InterruptedException e) {

                        }
                    } else {
                        try {
                            System.out.println("Stopping memory hog");
                            new MemoryHog().deflate();
                        } catch (InterruptedException e) {
                            
                        }
                    }
                }
            }
        }
    }

    private void log(UriInfo uriInfo, MultivaluedMap<String, String> params) {
        System.out.println("Path: " + uriInfo.getPath());
        System.out.println("Query parameters:");
        for(String key : params.keySet()) {
            System.out.println(String.format("  %s: %s", key, params.getFirst(key)));
        }
        // headers.entrySet().forEach(h -> System.out.println(h.getKey() + ": " + h.getValue()));
    }
    private void logHeaders(UriInfo uriInfo, MultivaluedMap<String, String> params) {
        System.out.println("Query Headers:");
        for(String key : params.keySet()) {
            System.out.println(String.format("  %s: %s", key, params.getFirst(key)));
        }
        // headers.entrySet().forEach(h -> System.out.println(h.getKey() + ": " + h.getValue()));
    }
}