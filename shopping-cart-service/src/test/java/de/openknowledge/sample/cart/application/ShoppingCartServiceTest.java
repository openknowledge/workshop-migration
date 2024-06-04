/*
 * Copyright (C) open knowledge GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.cart.application;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@MeecrowaveConfig
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "checkout-service", pactVersion = PactSpecVersion.V3)
public class ShoppingCartServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @Pact(consumer = "shopping-cart-service")
    public RequestResponsePact createOffer(PactDslWithProvider builder) throws IOException {
        return builder
          .given("No offers")
            .uponReceiving("POST request to create offer")
            .method("POST")
            .path("/offers")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("customerNumber", "0815")
                    .array("items")
                        .object()
                            .stringValue("productNumber", "42")
                            .numberValue("quantity", 1)
                        .closeObject()
                    .closeArray())
          .willRespondWith()
            .status(201)
            .matchHeader("Location", "http://.*", "http://localhost:4001/offers")
          .toPact();
    }

    @BeforeEach
    public void initializeRepository(MockServer mockServer) {
        System.setProperty("checkout-service.url", "http://localhost:" + mockServer.getPort());
    }

    @Test
    @PactTestFor(pactMethod = "createOffer")
    public void createOffer() {
        Response response = newClient().target("http://localhost:" + config.getHttpPort())
            .path("shopping-carts/0815")
            .request()
            .post(form(new Form()
                .param("itemNumber", "42")
                .param("itemQuantity", "1")));
        
        assertThat(response.getStatus()).isEqualTo(SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).startsWith("http://localhost:4001/offers");
    }
}
