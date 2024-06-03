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

import static javax.ws.rs.client.Entity.json;

import java.net.URI;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.checkout.domain.OfferNumber;
import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.address.City;
import de.openknowledge.sample.checkout.domain.address.HouseNumber;
import de.openknowledge.sample.checkout.domain.address.Street;
import de.openknowledge.sample.checkout.domain.address.ZipCode;
import de.openknowledge.sample.checkout.domain.offer.Offer;
import de.openknowledge.sample.checkout.domain.offer.OfferService;
import de.openknowledge.sample.checkout.domain.offer.payment.BankIdentifierCode;
import de.openknowledge.sample.checkout.domain.offer.payment.CardVerificationNumber;
import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardNumber;
import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.DirectBillingPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.Email;
import de.openknowledge.sample.checkout.domain.offer.payment.EmailPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.InternationalBankAccountNumber;
import de.openknowledge.sample.checkout.domain.offer.payment.Month;
import de.openknowledge.sample.checkout.domain.offer.payment.Owner;
import de.openknowledge.sample.checkout.domain.offer.payment.Year;
import de.openknowledge.sample.checkout.domain.order.Order;

@Path("/")
@ApplicationScoped
public class OfferResource  {

    @Inject
    private OfferBean offerBean;

    @Inject
    private OrderBean orderBean;

    @Inject
    @ConfigProperty(name = "external.url")
    private URI externalUrl;

    @Inject
    @ConfigProperty(name = "order-service.url")
    private URI orderServiceUrl;

    @Inject
    private Function<String, String> templateProcessor;

    @Inject
    private OfferService offerService;

    @POST
    @Consumes({ "application/json" })
    public Response createOffer(@Valid @NotNull Offer offer) {
        OfferNumber number = offerService.createOffer(offer);
        return Response.created(externalUrl.resolve("/offers/" + number.number() + "/payment")).build();
    }

    @GET
    @Path("/{offerNumber}/payment")
    @Produces({ "text/html" })
    public String showPaymentPage(@PathParam("offerNumber") @Valid OfferNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("payment");
    }

    @POST
    @Path("/{offerNumber}/credit-card-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setCreditCardPayment(
            @PathParam("offerNumber") @Valid OfferNumber number,
            @FormParam("name") @NotNull @Valid Owner name,
            @FormParam("number") @NotNull @Valid CreditCardNumber creditCardNumber,
            @FormParam("expiryMonth") @NotNull @Valid Month expiryMonth,
            @FormParam("expiryYear") @NotNull @Valid Year expiryYear,
            @FormParam("cvn") @NotNull @Valid CardVerificationNumber cvn) {
        offerService.setPayment(number, new CreditCardPayment(name, creditCardNumber, expiryMonth, expiryYear, cvn));
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @POST
    @Path("/{offerNumber}/direct-billing-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDirectBillingPayment(
            @PathParam("offerNumber") @Valid OfferNumber number,
            @FormParam("name") @NotNull @Valid Owner name,
            @FormParam("iban") @NotNull @Valid InternationalBankAccountNumber iban,
            @FormParam("bic") @NotNull @Valid BankIdentifierCode bic) {
        offerService.setPayment(number, new DirectBillingPayment(name, iban, bic));
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @POST
    @Path("/{offerNumber}/email-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setEmailPayment(@PathParam("offerNumber") @Valid OfferNumber number, @FormParam("email") Email email) {
        offerService.setPayment(number, new EmailPayment(email));
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @GET
    @Path("/{offerNumber}/billing-address")
    @Produces({ "text/html" })
    public String showBillingAddressPage(@PathParam("offerNumber") @Valid OfferNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("billing-address");
    }

    @POST
    @Path("/{offerNumber}/billing-address")
    @Consumes("application/x-www-form-urlencoded")
    public Response setBillingAddress(
            @PathParam("offerNumber") @Valid OfferNumber number,
            @Valid @NotNull Address address) {
        offerService.setBillingAddress(number, address);
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/delivery-address")).build();
    }

    @POST
    @Path("/{offerNumber}/delivery-address")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDeliveryAddress(
            @PathParam("offerNumber") @Valid OfferNumber number,
            @FormParam("sameAsBillingAddress") boolean sameAsBillingAddress,
            @FormParam("street") @Valid Street street,
            @FormParam("zipCode") @Valid ZipCode zipCode,
            @FormParam("city") @Valid City city,
            @FormParam("houseNumber") @Valid HouseNumber houseNumber) {
        offerService.setDeliveryAddress(number, sameAsBillingAddress, sameAsBillingAddress ? null: new Address(street, houseNumber, zipCode, city));
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/summary")).build();
    }

    @GET
    @Path("/{offerNumber}/delivery-address")
    @Produces({ "text/html" })
    public String showDeliveryAddressPage(@PathParam("offerNumber") @Valid OfferNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("delivery-address");
    }

    @GET
    @Path("/{offerNumber}/summary")
    @Produces({ "text/html" })
    public String showSummaryPage(@PathParam("offerNumber") @Valid OfferNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        Order order = offerService.createOrder(offerNumber);
        orderBean.setOrder(order);
        return templateProcessor.apply("summary");
    }

    @POST
    @Path("/{offerNumber}/orders")
    @Consumes("application/x-www-form-urlencoded")
    public Response createOrder(@PathParam("offerNumber") @Valid OfferNumber number) {
        Order order = offerService.createOrder(number);
        ClientBuilder.newClient().target(orderServiceUrl).path("orders").request().post(json(order));
        return Response.seeOther(externalUrl.resolve("/offers/" + number.number() + "/success")).build();
    }

    @GET
    @Path("/{offerNumber}/success")
    @Produces({ "text/html" })
    public String showSuccessPage(@PathParam("offerNumber") @Valid OfferNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("success");
    }
}
