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
package de.openknowledge.sample.onlineshop.bean;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotFoundException;

import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.repository.CustomerRepository;

@Named("addresses")
@RequestScoped
public class AddressBean {

    @Inject
    private CustomerRepository repository;
    private Address defaultBillingAddress;
    private Address defaultDeliveryAddress;
    private List<Address> addresses;

    @Inject
    private void loadAddresses(CustomerNumber number) {
    	CustomerAggregate customer = repository.findCustomer(number).orElseThrow(NotFoundException::new);
        defaultBillingAddress = customer.getDefaultBillingAddress().orElse(null);
        defaultDeliveryAddress = customer.getDefaultDeliveryAddress().orElse(null);
        addresses = customer.getAddresses();
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
