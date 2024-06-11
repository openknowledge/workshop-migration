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

import java.net.URI;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.onlineshop.domain.order.OrderNumber;
import de.openknowledge.sample.onlineshop.infrastructure.jpa.Transactional;
import de.openknowledge.sample.onlineshop.service.OrderService;

@ApplicationScoped
@Path("/orders")
public class OrderResource  {

    @Inject
    @ConfigProperty(name = "external.url")
    private URI url;
    @Inject
    private Function<String, String> templateProcessor;
    @Inject
    private OrderService orderService;

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response createOrder(@FormParam("offerNumber") @Valid OrderNumber number) {
        orderService.order(number);
        return Response.seeOther(url.resolve("/orders/" + number.number())).build();
    }

    @GET
    @Transactional
    @Path("/{orderNumber}")
    @Produces({ "text/html" })
    public String showSuccessPage(@PathParam("orderNumber") @Valid OrderNumber offerNumber) {
        return templateProcessor.apply("success");
    }
}
