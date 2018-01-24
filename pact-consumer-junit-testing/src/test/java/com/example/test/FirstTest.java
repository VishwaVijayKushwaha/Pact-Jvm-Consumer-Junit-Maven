package com.example.test;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;

public class FirstTest {
	@Test
	  public void testPact() {
	    RequestResponsePact pact = ConsumerPactBuilder
	      .consumer("Inventary status consumer")
	      .hasPactWith("Inventary status provider")
	      .uponReceiving("Request for available item")
	      .path("/hello")
	      .method("POST")
	      .body("{\"name\": \"harry\"}")
	      .willRespondWith()
	      .status(200)
	      .body("{\"hello\": \"harry\"}")
	      .toPact();

	    MockProviderConfig config = MockProviderConfig.createDefault();
	    
	    PactVerificationResult result = runConsumerTest(pact, config, mockServer->{
	        Map<String, Object> expectedResponse = new HashMap<>();
	        expectedResponse.put("hello", "harry");
	        assertEquals(expectedResponse, new ConsumerClient(mockServer.getUrl()).post("/hello",
	            "{\"name\": \"harry\"}", ContentType.APPLICATION_JSON));
	      }
	    );

	    if (result instanceof PactVerificationResult.Error) {
	      throw new RuntimeException(((PactVerificationResult.Error)result).getError());
	    }

	    assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	  }
}
