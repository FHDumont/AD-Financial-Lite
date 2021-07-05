package com.appdynamics.sample.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class RestClient {

	Client restClient = null;
	
	public RestClient() {
		restClient = ClientBuilder.newClient();
	}

	public WebTarget getWebTarget(String serviceUrl) {
		
		if (serviceUrl == null) {
			throw new IllegalArgumentException("Error getting rest client, baseAddress and service cannot be null");
		}
		
		
		return restClient.target(serviceUrl);
	}
}

