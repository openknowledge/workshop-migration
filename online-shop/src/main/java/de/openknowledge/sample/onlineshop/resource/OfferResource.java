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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.seeOther;

import java.net.URI;
import java.util.List;
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
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.onlineshop.bean.OfferBean;
import de.openknowledge.sample.onlineshop.bean.OrderBean;
import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.address.City;
import de.openknowledge.sample.onlineshop.domain.address.HouseNumber;
import de.openknowledge.sample.onlineshop.domain.address.Street;
import de.openknowledge.sample.onlineshop.domain.address.ZipCode;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.domain.order.OrderSummary;
import de.openknowledge.sample.onlineshop.domain.order.OrderNumber;
import de.openknowledge.sample.onlineshop.domain.order.ProductNumber;
import de.openknowledge.sample.onlineshop.domain.order.Quantity;
import de.openknowledge.sample.onlineshop.domain.payment.BankIdentifierCode;
import de.openknowledge.sample.onlineshop.domain.payment.CardVerificationNumber;
import de.openknowledge.sample.onlineshop.domain.payment.CreditCardNumber;
import de.openknowledge.sample.onlineshop.domain.payment.CreditCardPayment;
import de.openknowledge.sample.onlineshop.domain.payment.DirectBillingPayment;
import de.openknowledge.sample.onlineshop.domain.payment.Email;
import de.openknowledge.sample.onlineshop.domain.payment.EmailPayment;
import de.openknowledge.sample.onlineshop.domain.payment.InternationalBankAccountNumber;
import de.openknowledge.sample.onlineshop.domain.payment.Month;
import de.openknowledge.sample.onlineshop.domain.payment.Owner;
import de.openknowledge.sample.onlineshop.domain.payment.Year;
import de.openknowledge.sample.onlineshop.domain.shoppingcart.ShoppingCart;
import de.openknowledge.sample.onlineshop.service.OrderService;

@Path("/offers")
@ApplicationScoped
public class OfferResource  {

    @Inject
    private OfferBean offerBean;

    @Inject
    private OrderBean orderBean;

    @Inject
    @ConfigProperty(name = "external.url")
    private URI url;

    @Inject
    private Function<String, String> templateProcessor;

    @Inject
    private OrderService offerService;

    @POST
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createOffer(@FormParam("customerNumber") CustomerNumber customerNumber, @FormParam("itemNumber") List<ProductNumber> productNumbers, @FormParam("itemQuantity") List<Quantity> quantities) {
        System.out.println("createOffer");
        OrderNumber offerNumber = offerService.createOffer(new ShoppingCart(customerNumber, productNumbers, quantities));
        return seeOther(url.resolve("/offers/" + offerNumber.number() + "/payment")).build();
    }

    @GET
    @Path("/{offerNumber}/payment")
    @Produces({ "text/html" })
    public String showPaymentPage(@PathParam("offerNumber") @Valid OrderNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("payment");
    }

    @POST
    @Path("/{offerNumber}/credit-card-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setCreditCardPayment(
            @PathParam("offerNumber") @Valid OrderNumber number,
            @FormParam("name") @NotNull @Valid Owner name,
            @FormParam("number") @NotNull @Valid CreditCardNumber creditCardNumber,
            @FormParam("expiryMonth") @NotNull @Valid Month expiryMonth,
            @FormParam("expiryYear") @NotNull @Valid Year expiryYear,
            @FormParam("cvn") @NotNull @Valid CardVerificationNumber cvn) {
        offerService.setPayment(number, new CreditCardPayment(name, creditCardNumber, expiryMonth, expiryYear, cvn));
        return Response.seeOther(url.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @POST
    @Path("/{offerNumber}/direct-billing-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDirectBillingPayment(
            @PathParam("offerNumber") @Valid OrderNumber number,
            @FormParam("name") @NotNull @Valid Owner name,
            @FormParam("iban") @NotNull @Valid InternationalBankAccountNumber iban,
            @FormParam("bic") @NotNull @Valid BankIdentifierCode bic) {
        offerService.setPayment(number, new DirectBillingPayment(name, iban, bic));
        return Response.seeOther(url.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @POST
    @Path("/{offerNumber}/email-payment")
    @Consumes("application/x-www-form-urlencoded")
    public Response setEmailPayment(@PathParam("offerNumber") @Valid OrderNumber number, @FormParam("email") Email email) {
        offerService.setPayment(number, new EmailPayment(email));
        return Response.seeOther(url.resolve("/offers/" + number.number() + "/billing-address")).build();
    }

    @GET
    @Path("/{offerNumber}/billing-address")
    @Produces({ "text/html" })
    public String showBillingAddressPage(@PathParam("offerNumber") @Valid OrderNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("billing-address");
    }

    @POST
    @Path("/{offerNumber}/billing-address")
    @Consumes("application/x-www-form-urlencoded")
    public Response setBillingAddress(
            @PathParam("offerNumber") @Valid OrderNumber number,
            @Valid @NotNull Address address) {
        offerService.setBillingAddress(number, address);
        return Response.seeOther(url.resolve("/offers/" + number.number() + "/delivery-address")).build();
    }

    @POST
    @Path("/{offerNumber}/delivery-address")
    @Consumes("application/x-www-form-urlencoded")
    public Response setDeliveryAddress(
            @PathParam("offerNumber") @Valid OrderNumber number,
            @FormParam("sameAsBillingAddress") boolean sameAsBillingAddress,
            @FormParam("street") @Valid Street street,
            @FormParam("zipCode") @Valid ZipCode zipCode,
            @FormParam("city") @Valid City city,
            @FormParam("houseNumber") @Valid HouseNumber houseNumber) {
        offerService.setDeliveryAddress(number, sameAsBillingAddress, sameAsBillingAddress ? null: new Address(street, houseNumber, zipCode, city));
        return Response.seeOther(url.resolve("/offers/" + number.number() + "/summary")).build();
    }

    @GET
    @Path("/{offerNumber}/delivery-address")
    @Produces({ "text/html" })
    public String showDeliveryAddressPage(@PathParam("offerNumber") @Valid OrderNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        return templateProcessor.apply("delivery-address");
    }

    @GET
    @Path("/{offerNumber}/summary")
    @Produces({ "text/html" })
    public String showSummaryPage(@PathParam("offerNumber") @Valid OrderNumber offerNumber) {
        offerBean.setNumber(offerNumber);
        OrderSummary order = offerService.createSummary(offerNumber);
        orderBean.setOrder(order);
        return templateProcessor.apply("summary");
    }
}
