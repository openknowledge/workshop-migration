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
package de.openknowledge.sample.onlineshop.resource;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.seeOther;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.domain.order.OrderAggregate;
import de.openknowledge.sample.onlineshop.domain.order.OrderStatus;
import de.openknowledge.sample.onlineshop.domain.order.ProductNumber;
import de.openknowledge.sample.onlineshop.domain.order.Quantity;
import de.openknowledge.sample.onlineshop.repository.CustomerRepository;
import de.openknowledge.sample.onlineshop.repository.OrderRepository;

@Path("/shopping-carts/{customerNumber}")
@ApplicationScoped
public class ShoppingCartResource {

    @Inject
    @ConfigProperty(name = "checkout-service.url")
    private URI checkoutServiceUrl;

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private CustomerRepository customerRepository;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getShoppingCart(@PathParam("customerNumber") CustomerNumber number) throws IOException {
    	CustomerAggregate customer = customerRepository.findCustomer(number).orElseThrow(NotFoundException::new);
        OrderAggregate aggregate = new OrderAggregate(customer, OrderStatus.SHOPPING_CART);
        orderRepository.persist(aggregate);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/resources/shopping-cart.html");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                StringWriter content = new StringWriter()) {
            buffer.lines().forEach(content::write);
            return Response.ok(content.toString()).type(MediaType.TEXT_HTML_TYPE).build();
        }
    }

    @POST
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createOffer(@PathParam("customerNumber") CustomerNumber customerNumber, @FormParam("itemNumber") List<ProductNumber> productNumbers, @FormParam("itemQuantity") List<Quantity> quantities) {
        Response response = newClient()
            .target(checkoutServiceUrl)
            .path("offers")
            .request()
            .post(Entity.entity(new Offer(customerNumber, productNumbers, quantities), MediaType.APPLICATION_JSON_TYPE));
        return seeOther(response.getLocation()).build();
    }
}
