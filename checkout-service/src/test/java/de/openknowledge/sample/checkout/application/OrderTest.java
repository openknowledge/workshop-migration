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
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.SEE_OTHER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.address.City;
import de.openknowledge.sample.checkout.domain.address.HouseNumber;
import de.openknowledge.sample.checkout.domain.address.Street;
import de.openknowledge.sample.checkout.domain.address.ZipCode;
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

@Testcontainers
@MeecrowaveConfig
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "order-service", pactVersion = PactSpecVersion.V3)
public class OrderTest {

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
        Flyway flyway = Flyway
            .configure()
            .dataSource(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword())
            .cleanDisabled(false).load();
        flyway.clean();
        flyway.migrate();
    }

    @BeforeEach
    public void initializeRepository(MockServer mockServer) {
        System.setProperty("order-service.url", "http://localhost:" + mockServer.getPort());
        String url = "http://localhost:" + config.getHttpPort();
        System.setProperty("external.url", url);
        Response response = newClient().target(url).path("offers")
            .request().post(Entity.json("""
                    {
                        "customerNumber": "0815",
                        "items": [
                            {
                                "productNumber": "0816",
                                "quantity": 1
                            },
                            {
                                "productNumber": "0817",
                                "quantity": 1
                            }
                        ]
                    }
                    """));
        String paymentPath = response.getLocation().getPath();
        String offerPath = paymentPath.substring(0, paymentPath.lastIndexOf("/"));
        String offerNumber = offerPath.substring(offerPath.lastIndexOf("/") + 1);
        offerUri = URI.create(url + "/offers/" + offerNumber);
    }

    @Pact(consumer = "checkout-service")
    public RequestResponsePact createOrderWithCreditCard(PactDslWithProvider builder) throws IOException {
        return builder
          .given("No offers")
            .uponReceiving("POST request to create order")
            .method("POST")
            .path("/orders")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("customerNumber", "0815")
                    .array("items")
                        .object()
                            .stringValue("productNumber", "0816")
                            .numberValue("quantity", 1)
                        .closeObject()
                        .object()
                            .stringValue("productNumber", "0817")
                            .numberValue("quantity", 1)
                        .closeObject()
                    .closeArray()
                    .object("payment")
                        .stringValue("name", "Max Mustermann")
                        .stringValue("number", "1234567890123456")
                        .numberValue("expiryMonth", 1)
                        .numberValue("expiryYear", 2024)
                        .stringValue("cvn", "123")
                    .closeObject()
                    .object("billingAddress")
                        .stringValue("street", "Poststr.")
                        .stringValue("houseNumber", "1")
                        .stringValue("zipCode", "26122")
                        .stringValue("city", "Oldenburg")
                    .closeObject()
                    .object("deliveryAddress")
                    .stringValue("street", "II. Hagen")
                    .stringValue("houseNumber", "7")
                    .stringValue("zipCode", "45127")
                    .stringValue("city", "Essen")
                    .closeObject())
          .willRespondWith()
            .status(201)
            .matchHeader("Location", "http://.*", "http://localhost:4001/offers")
          .toPact();
    }

    @PactTestFor(pactMethod = "createOrderWithCreditCard")
    @Test
    public void createOrderWithCreditCard() {
        setPayment(new CreditCardPayment(
                new Owner("Max Mustermann"),
                new CreditCardNumber("1234567890123456"),
                new Month(1),
                new Year(2024),
                new CardVerificationNumber("123")));
        setBillingAddress(new Address(new Street("Poststr."), new HouseNumber("1"), new ZipCode("26122"), new City("Oldenburg")));
        setDeliveryAddress(new Address(new Street("II. Hagen"), new HouseNumber("7"), new ZipCode("45127"), new City("Essen")));

        Response response = newClient().target(offerUri)
            .path("orders")
            .request()
            .post(form(new Form()));
        
        assertThat(response.getStatus()).isEqualTo(SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).isEqualTo(offerUri + "/success");
    }

    @Pact(consumer = "checkout-service")
    public RequestResponsePact createOrderWithDirectBilling(PactDslWithProvider builder) throws IOException {
        return builder
          .given("No offers")
            .uponReceiving("POST request to create order")
            .method("POST")
            .path("/orders")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("customerNumber", "0815")
                    .array("items")
                        .object()
                            .stringValue("productNumber", "0816")
                            .numberValue("quantity", 1)
                        .closeObject()
                        .object()
                            .stringValue("productNumber", "0817")
                            .numberValue("quantity", 1)
                        .closeObject()
                    .closeArray()
                    .object("payment")
                        .stringValue("name", "Max Mustermann")
                        .stringValue("iban", "DE1234567890123456")
                        .stringValue("bic", "1234567890")
                    .closeObject()
                    .object("billingAddress")
                        .stringValue("street", "Poststr.")
                        .stringValue("houseNumber", "1")
                        .stringValue("zipCode", "26122")
                        .stringValue("city", "Oldenburg")
                    .closeObject()
                    .object("deliveryAddress")
                    .stringValue("street", "II. Hagen")
                    .stringValue("houseNumber", "7")
                    .stringValue("zipCode", "45127")
                    .stringValue("city", "Essen")
                    .closeObject())
          .willRespondWith()
            .status(201)
            .matchHeader("Location", "http://.*", "http://localhost:4001/offers")
          .toPact();
    }

    @PactTestFor(pactMethod = "createOrderWithDirectBilling")
    @Test
    public void createOrderWithDirectBilling() {
        setPayment(new EmailPayment(new Email("max.mustermann@openknowledge.de")));
        setPayment(new DirectBillingPayment(
                new Owner("Max Mustermann"),
                new InternationalBankAccountNumber("DE1234567890123456"),
                new BankIdentifierCode("1234567890")));
        setBillingAddress(new Address(new Street("Poststr."), new HouseNumber("1"), new ZipCode("26122"), new City("Oldenburg")));
        setDeliveryAddress(new Address(new Street("II. Hagen"), new HouseNumber("7"), new ZipCode("45127"), new City("Essen")));

        Response response = newClient().target(offerUri)
            .path("orders")
            .request()
            .post(form(new Form()));
        
        assertThat(response.getStatus()).isEqualTo(SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).isEqualTo(offerUri + "/success");
    }

    @Pact(consumer = "checkout-service")
    public RequestResponsePact createOrderWithEmail(PactDslWithProvider builder) throws IOException {
        return builder
          .given("No offers")
            .uponReceiving("POST request to create order")
            .method("POST")
            .path("/orders")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("customerNumber", "0815")
                    .array("items")
                        .object()
                            .stringValue("productNumber", "0816")
                            .numberValue("quantity", 1)
                        .closeObject()
                        .object()
                            .stringValue("productNumber", "0817")
                            .numberValue("quantity", 1)
                        .closeObject()
                    .closeArray()
                    .object("payment")
                        .stringValue("email", "max.mustermann@openknowledge.de")
                    .closeObject()
                    .object("billingAddress")
                        .stringValue("street", "Poststr.")
                        .stringValue("houseNumber", "1")
                        .stringValue("zipCode", "26122")
                        .stringValue("city", "Oldenburg")
                    .closeObject()
                    .object("deliveryAddress")
                    .stringValue("street", "II. Hagen")
                    .stringValue("houseNumber", "7")
                    .stringValue("zipCode", "45127")
                    .stringValue("city", "Essen")
                    .closeObject())
          .willRespondWith()
            .status(201)
            .matchHeader("Location", "http://.*", "http://localhost:4001/offers")
          .toPact();
    }

    @PactTestFor(pactMethod = "createOrderWithEmail")
    @Test
    public void createOrderWithEmail() {
        setPayment(new EmailPayment(new Email("max.mustermann@openknowledge.de")));
        setBillingAddress(new Address(new Street("Poststr."), new HouseNumber("1"), new ZipCode("26122"), new City("Oldenburg")));
        setDeliveryAddress(new Address(new Street("II. Hagen"), new HouseNumber("7"), new ZipCode("45127"), new City("Essen")));

        Response response = newClient().target(offerUri)
            .path("orders")
            .request()
            .post(form(new Form()));
        
        assertThat(response.getStatus()).isEqualTo(SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).isEqualTo(offerUri + "/success");
    }

    private void setPayment(DirectBillingPayment directBillingPayment) {
        newClient().target(offerUri)
                .path("direct-billing-payment")
                .request()
                .post(form(new Form()
                        .param("name", directBillingPayment.name().name())
                        .param("iban", directBillingPayment.iban().number())
                        .param("bic", directBillingPayment.bic().code())));
    }

    private void setPayment(CreditCardPayment creditCardPayment) {
        newClient().target(offerUri)
                .path("credit-card-payment")
                .request()
                .post(form(new Form()
                        .param("name", creditCardPayment.name().name())
                        .param("number", creditCardPayment.number().number())
                        .param("expiryMonth", Integer.toString(creditCardPayment.expiryMonth().month()))
                        .param("expiryYear", Integer.toString(creditCardPayment.expiryYear().year()))
                        .param("cvn", creditCardPayment.cvn().number())));
    }

    private void setPayment(EmailPayment emailPayment) {
        newClient().target(offerUri)
            .path("email-payment")
            .request()
            .post(form(new Form().param("email", emailPayment.email().address())));
    }

    private void setBillingAddress(Address address) {
        newClient().target(offerUri)
            .path("billing-address")
            .request()
            .post(form(new Form()
                    .param("street", address.street().name())
                    .param("houseNumber", address.houseNumber().number())
                    .param("zipCode", address.zipCode().code())
                    .param("city", address.city().name())));
    }

    private void setDeliveryAddress(Address address) {
        newClient().target(offerUri)
            .path("delivery-address")
            .request()
            .post(form(new Form()
                    .param("street", address.street().name())
                    .param("houseNumber", address.houseNumber().number())
                    .param("zipCode", address.zipCode().code())
                    .param("city", address.city().name())));
    }
}
