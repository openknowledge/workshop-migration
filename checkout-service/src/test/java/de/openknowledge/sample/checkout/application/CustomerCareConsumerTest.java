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
package de.openknowledge.sample.checkout.application;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flywaydb.core.Flyway.configure;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Response;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@Testcontainers
@MeecrowaveConfig
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "customer-care-service", pactVersion = PactSpecVersion.V3)
public class CustomerCareConsumerTest {

    @Container
    private static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16.1");

    @ConfigurationInject
    private Meecrowave.Builder config;

    private URI offerUri;

    @BeforeAll
    public static void configureDatabase() {
        String jdbcUrl = postgresqlContainer.getJdbcUrl();
        String username = postgresqlContainer.getUsername();
        String password = postgresqlContainer.getPassword();
        System.setProperty("jakarta.persistence.jdbc.url", jdbcUrl);
        System.setProperty("jakarta.persistence.jdbc.user", username);
        System.setProperty("jakarta.persistence.jdbc.password", password);
        Flyway flyway = configure().dataSource(jdbcUrl, username, password).cleanDisabled(false).load();
        flyway.clean();
        flyway.migrate();
    }

    @BeforeEach
    public void initializeRepository(MockServer mockServer) {
        System.setProperty("customer-care-service.url", "http://localhost:" + mockServer.getPort());
        String url = "http://localhost:" + config.getHttpPort();
        System.setProperty("external.url", url);
        Response response = newClient().target(url).path("offers")
            .request().post(json("""
                    {
                        "customerNumber": "0815",
                        "items": [
                            {
                                "productNumber": "4711",
                                "quantity": 1
                            }
                        ]
                    }
            """));
        String paymentPath = response.getLocation().getPath();
        String offerPath = paymentPath.substring(0, paymentPath.lastIndexOf("/"));
        String offerNumber = offerPath.substring(offerPath.lastIndexOf("/"));
        offerUri = URI.create(url + "/offers/" + offerNumber);
    }

    @Pact(consumer = "checkout-service")
    public RequestResponsePact getAddresses(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Customer 0815")
            .uponReceiving("GET request to get default billing address")
            .method("GET")
            .path("/customers/0815/addresses/default-billing")
          .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
              .stringValue("street", "Poststr.")
              .stringValue("houseNumber", "1")
              .stringValue("zipCode", "26122")
              .stringValue("city", "Oldenburg"))
          .given("Customer 0815")
            .uponReceiving("GET request to get default delivery address")
            .method("GET")
            .path("/customers/0815/addresses/default-delivery")
          .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
              .stringValue("street", "II. Hagen")
              .stringValue("houseNumber", "7")
              .stringValue("zipCode", "45127")
              .stringValue("city", "Essen"))
          .given("Customer 0815")
            .uponReceiving("GET request to get all addresses")
            .method("GET")
            .path("/customers/0815/addresses")
          .willRespondWith()
            .status(200)
            .body(new PactDslJsonArray()
              .object()
                .stringValue("street", "Poststr.")
                .stringValue("houseNumber", "1")
                .stringValue("zipCode", "26122")
                .stringValue("city", "Oldenburg")
              .closeObject()
              .object()
                .stringValue("street", "II. Hagen")
                .stringValue("houseNumber", "7")
                .stringValue("zipCode", "45127")
                .stringValue("city", "Essen")
              .closeObject())
          .toPact();
    }

    @PactTestFor(pactMethod = "getAddresses")
    @Test
    public void showBillingAddressPage() {
        String billingAddressPage = newClient().target(offerUri)
            .path("billing-address")
            .request()
            .accept(TEXT_HTML_TYPE)
            .get(String.class);

        assertThat(billingAddressPage).contains("Poststr.");
    }
}
