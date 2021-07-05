package com.appdynamics.sample.service;

public class DemoConstants {

	public static final String ORDER_PROCESSING_URL1="service/";
	public static final String ORDER_PROCESSING_URL2="service/rest/orderProcessing";
	public static final String ORDER_PROCESSING_URL3="service/rest/orderProcessing";
	public static final String SERVICE_MOCK_HOSTNAME="java-services-mock";
	public static final Integer SERVICE_MOCK_PORT=9999;
	
	public enum ENDPOINT { 
		AUTHENTICATE("/v1/oauth2/token"),
		VAULT_ADD_CARD("/v1/vault/credit-cards/"),
		PAYMENT_CREATE("/v1/payments/payment"), 
		PAYMENT_DETAILS("/v1/payments/details"),
		PAYMENT_LIST("/v1/payments/payment");

		private String endpoint = null;

		ENDPOINT(String endpoint) {
			this.endpoint = endpoint;
		}
		
		public String getEndpoint() {
			return endpoint;
		}
	}

	public static String getServiceMockBaseAddress() {
		return String.format("http://%s:%d", SERVICE_MOCK_HOSTNAME, SERVICE_MOCK_PORT);
	}

	/**
	 * 
	 * @param endpoint should be one of our endpoint constants
	 * @return
	 */
	public static String getServiceMockUrl(ENDPOINT service) {
		String baseAddress = getServiceMockBaseAddress();

		return baseAddress + service.getEndpoint();
	}
}
