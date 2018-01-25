package com.example.test;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;

public class PactGetPostTest {
	
	@Test
	public void getTest() {

		String path = "/hello";
		String responseBody = "{\"hello\":\"harry\"}";

		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("Inventary status consumer")
				.hasPactWith("Inventary status provider")
				.uponReceiving("Request for available item")
				.path(path)
				.query("itemNo=1234&status=Available")
				.method(HttpGet.METHOD_NAME)
				.willRespondWith()
				.status(200)
				.body(responseBody)
				.toPact();

		MockProviderConfig config = MockProviderConfig.createDefault();

		PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
			String response = Request.Get(mockServer.getUrl() + path + "?itemNo=1234&status=Available")
					.addHeader("testreqheader", "testreqheadervalue")
					.execute().returnContent().asString();
			Assert.assertEquals("Response is invalid.", response, responseBody);
		});

		if (result instanceof PactVerificationResult.Error) {
			throw new RuntimeException(((PactVerificationResult.Error) result).getError());
		}

		assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}
}
