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
package de.openknowledge.sample.order.application;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.Test;

@MeecrowaveConfig
public class OrderTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @Test
    public void createOffer() throws MalformedURLException {
        String url = "http://localhost:" + config.getHttpPort();
        Response response = newClient().target(url).path("orders")
            .request().post(Entity.json("""
{
    "customerNumber": "0815",
    "items": [
        {
            "productNumber": "4711",
            "quantity": 1
        }
    ],
    "payment": {
        "email": "max.mustermann@openknowledge.de"
    },
    "billingAddress": {
        "street": "Poststr.",
        "houseNumber": "1",
        "zipCode": "26122",
        "city": "Oldenburg"
    },
    "deliveryAddress": {
        "street": "Poststr.",
        "houseNumber": "1",
        "zipCode": "26122",
        "city": "Oldenburg"
    }
}
                    """));
        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }
}
