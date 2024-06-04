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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.openknowledge.sample.order.domain.Order;
import de.openknowledge.sample.order.domain.OrderNumber;
import de.openknowledge.sample.order.domain.OrdersApiRepository;

@ApplicationScoped
@Path("/orders")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:36:32.859193Z[Etc/UTC]")
public class OrdersApi  {

    @Inject
    private OrdersApiRepository repository;

    @POST
    
    @Consumes({ "application/json" })
    
    public Response createOrder(@Valid Order order, @Context UriInfo uri) {
        OrderNumber orderNumber = repository.createOrder(order);
        return Response.created(uri.getAbsolutePathBuilder().path(orderNumber.orderNumber()).build()).build();
    }

    @PUT
    @Path("/{orderNumber}")
    @Consumes({ "application/json" })
    
    public Response updateOrder(@PathParam("orderNumber") @Valid OrderNumber orderNumber, @Valid Order order) {
        try {
            repository.updateOrder(orderNumber, order);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
