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
import static javax.ws.rs.core.Response.Status.SEE_OTHER;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MeecrowaveConfig
public class AddressesTest {

    @Container
    private static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16.1");

    @ConfigurationInject
    private Meecrowave.Builder config;

    private URI offerUri;

    @BeforeAll
    public static void configureDatabase() {
        System.setProperty("jakarta.persistence.jdbc.url", postgresqlContainer.getJdbcUrl());
        System.setProperty("jakarta.persistence.jdbc.user", postgresqlContainer.getUsername());
        System.setProperty("jakarta.persistence.jdbc.password", postgresqlContainer.getPassword());
    }

    @BeforeEach
    public void initializeRepository() {
        String url = "http://localhost:" + config.getHttpPort();
        System.setProperty("external.url", url);
        Response response = newClient().target(url).path("offers")
            .request().post(Entity.json("""
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

    @Test
    public void setBillingAddress() {
        Response response = newClient().target(offerUri)
            .path("billing-address")
            .request()
            .post(Entity.form(new Form()
                    .param("street", "Poststr.")
                    .param("houseNumber", "1")
                    .param("zipCode", "26122")
                    .param("city", "Oldenburg")));
        
        assertThat(response.getStatus()).isEqualTo(SEE_OTHER.getStatusCode());
    }

    @Test
    public void setInvalidBillingAddress() {
        Response response = newClient().target(offerUri)
            .path("billing-address")
            .request()
            .post(Entity.form(new Form()
                    .param("street", "Poststr.")
                    .param("houseNumber", "1")
                    .param("city", "Oldenburg")));
        
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }
}
