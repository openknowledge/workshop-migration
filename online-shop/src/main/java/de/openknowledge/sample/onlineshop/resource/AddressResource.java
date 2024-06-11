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

import static java.lang.String.format;

import java.net.URI;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.onlineshop.bean.CustomerBean;
import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.infrastructure.jpa.Transactional;
import de.openknowledge.sample.onlineshop.repository.CustomerRepository;

@ApplicationScoped
@Path("/customers/{customerNumber}/addresses")
public class AddressResource  {

    @Inject
    @ConfigProperty(name = "external.url")
    private URI externalUrl;
    @Inject
    private CustomerBean customerBean;
    @Inject
    private Function<String, String> templateProcessor;
    @Inject
    private CustomerRepository repository;

    @POST
    @Transactional
    @Consumes({ "application/json" })
    public Response addAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber, @Valid @NotNull Address address) {
        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        customer.addAddress(address);
        return Response.ok().build();
    }

    @GET
    @Produces({ "text/html" })
    public String showAddressPage(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        customerBean.setCustomer(customer);
        return templateProcessor.apply("addresses");
    }

    @GET
    @Produces({ "application/json" })
    public Response getAddresses(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        return Response.ok(customer.getAddresses()).build();
    }

    @GET
    @Path("/default-billing")
    @Produces({ "application/json" })
    public Address getDefaultBillingAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        return customer.getDefaultBillingAddress().orElseThrow(NotFoundException::new);
    }

    @POST
    @Transactional
    @Path("/default-billing")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDefaultBillingAddress(
        @PathParam("customerNumber") @Valid CustomerNumber customerNumber,
        @NotNull @Valid Address address) {

        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        customer.setDefaultBillingAddress(address);
        return Response.seeOther(getAddressesUri(customerNumber)).build();
    }

    @GET
    @Path("/default-delivery")
    @Produces({ "application/json" })
    public Address getDefaultDeliveryAddress(@PathParam("customerNumber") @Valid CustomerNumber customerNumber) {
        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        return customer.getDefaultDeliveryAddress().orElseThrow(NotFoundException::new);
    }

    @POST
    @Transactional
    @Path("/default-delivery")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDefaultDeliveryAddress(
        @PathParam("customerNumber") @Valid CustomerNumber customerNumber,
        @NotNull @Valid Address address) {

        CustomerAggregate customer = repository.findCustomer(customerNumber).orElseThrow(NotFoundException::new);
        customer.setDefaultDeliveryAddress(address);
        return Response.seeOther(getAddressesUri(customerNumber)).build();
    }

    private URI getAddressesUri(CustomerNumber customerNumber) {
        return externalUrl.resolve(format("/customers/%s/addresses", customerNumber.number()));
    }
}
