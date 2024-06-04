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
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.seeOther;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.cart.domain.CustomerNumber;
import de.openknowledge.sample.cart.domain.Offer;
import de.openknowledge.sample.cart.domain.ProductNumber;
import de.openknowledge.sample.cart.domain.Quantity;

@Path("/{customerNumber}")
@ApplicationScoped
public class ShoppingCartResource {

    private final static Logger LOGGER = Logger.getLogger(ShoppingCartResource.class.getSimpleName());

    @Inject
    @ConfigProperty(name = "checkout-service.url")
    private URI checkoutServiceUrl;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getShoppingCart(@PathParam("customerNumber") CustomerNumber number) throws IOException {
        LOGGER.info("RESTful call 'GET shopping cart'");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/shopping-cart.html");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                StringWriter content = new StringWriter()) {
            buffer.lines().forEach(content::write);
            return Response.ok(content.toString()).type(MediaType.TEXT_HTML_TYPE).build();
        }
    }

    @POST
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createOffer(@PathParam("customerNumber") CustomerNumber customerNumber, @FormParam("itemNumber") List<ProductNumber> productNumbers, @FormParam("itemQuantity") List<Quantity> quantities) {
        LOGGER.info("RESTful call 'POST offer'");
        Response response = newClient()
            .target(checkoutServiceUrl)
            .path("offers")
            .request()
            .post(entity(new Offer(customerNumber, productNumbers, quantities), MediaType.APPLICATION_JSON_TYPE));
        System.out.println(response.readEntity(String.class));
        return seeOther(response.getLocation()).build();
    }
}
