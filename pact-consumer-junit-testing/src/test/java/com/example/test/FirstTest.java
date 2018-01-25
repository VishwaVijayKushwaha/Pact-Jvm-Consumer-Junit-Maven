package com.example.test;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;

public class FirstTest {
	@Test
	  public void testPact() {
		
		String path = "/hello";
		String requestBody = "{\"name\":\"harry\"}";
		String responseBody = "{\"hello\":\"harry\"}";
		
	    RequestResponsePact pact = ConsumerPactBuilder
	      .consumer("Inventary status consumer")
	      .hasPactWith("Inventary status provider")
	      .uponReceiving("Request for available item")
	      .path(path)
	      .method(HttpPost.METHOD_NAME)
	      .body(requestBody)
	      .willRespondWith()
	      .status(200)
	      .body(responseBody)
	      .toPact();

	    MockProviderConfig config = MockProviderConfig.createDefault();
	    
	    PactVerificationResult result = runConsumerTest(pact, config, mockServer->{
	    	String response = Request.Post(mockServer.getUrl() + path)
            	.addHeader("testreqheader", "testreqheadervalue")
            	.bodyString(requestBody, ContentType.APPLICATION_JSON)
            	.execute().returnContent().asString();
	    	Assert.assertEquals("Response is invalid.", response, responseBody);
	      }
	    );

	    if (result instanceof PactVerificationResult.Error) {
	      throw new RuntimeException(((PactVerificationResult.Error)result).getError());
	    }

	    assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	  }
}
