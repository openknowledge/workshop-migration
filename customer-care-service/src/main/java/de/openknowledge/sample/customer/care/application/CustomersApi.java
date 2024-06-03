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
package de.openknowledge.sample.customer.care.application;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.openknowledge.sample.customer.care.domain.address.Address;
import de.openknowledge.sample.customer.care.domain.address.CustomerNumber;
import de.openknowledge.sample.customer.care.domain.address.CustomersApiRepository;

@ApplicationScoped
@Path("/customers/{customerNumber}/addresses")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:32:42.502228Z[Etc/UTC]")
public class CustomersApi  {

    @Inject
    private CustomersApiRepository repository;

    @POST
    
    @Consumes({ "application/json" })
    
    public Response addAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber, @Valid @NotNull Address address) {
        repository.addAddress(customerNumber, address);
        return Response.ok().build();
    }
    @GET
    
    
    @Produces({ "application/json" })
    public Response getAddresses(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        return Response.ok(repository.getAddresses(customerNumber)).build();
    }
    @GET
    @Path("/default-billing")
    
    @Produces({ "application/json" })
    public Response getDefaultBillingAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        return repository.getDefaultBillingAddress(customerNumber)
            .map(address -> Response.ok(address).build())
            .orElseGet(() -> Response.status(NOT_FOUND).build());
    }
    @GET
    @Path("/default-delivery")
    
    @Produces({ "application/json" })
    public Response getDefaultDeliveryAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        return repository.getDefaultDeliveryAddress(customerNumber)
                .map(address -> Response.ok(address).build())
                .orElseGet(() -> Response.status(NOT_FOUND).build());
    }
}
