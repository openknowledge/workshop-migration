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

import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.customer.CustomerNumber;
import de.openknowledge.sample.checkout.infrastructure.jaxrs.RecordListMessageBodyReader;
import de.openknowledge.sample.checkout.infrastructure.jaxrs.RecordMessageBodyReader;

@Named("addresses")
@RequestScoped
public class AddressBean {

    @Inject
    @ConfigProperty(name = "customer-care-service.url")
    private URI url;

    private Address defaultBillingAddress;
    private Address defaultDeliveryAddress;
    private List<Address> addresses;

    @Inject
    private void loadAddresses(CustomerNumber number) {
        WebTarget addressListTarget = newClient()
                .target(url)
                .path("customers/" + number.number() + "/addresses");
        WebTarget defaultBillingAddressTarget = addressListTarget.path("default-billing").register(RecordMessageBodyReader.class);
        WebTarget defaultDeliveryAddressTarget = addressListTarget.path("default-delivery").register(RecordMessageBodyReader.class);
        defaultBillingAddress = defaultBillingAddressTarget.request().get(Address.class);
        defaultDeliveryAddress = defaultDeliveryAddressTarget.request().get(Address.class);
        addresses = addressListTarget.register(RecordListMessageBodyReader.class).request().get(new GenericType<List<Address>>() {});
    }

    public Address getDefaultBilling() {
        return defaultBillingAddress;
    }

    public Address getDefaultDelivery() {
        return defaultDeliveryAddress;
    }

    public List<Address> getList() {
        return addresses;
    }
}
