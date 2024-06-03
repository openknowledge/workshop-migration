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
package de.openknowledge.sample.customer.care.domain.address;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomersApiRepository {

    private Map<CustomerNumber, Set<Address>> addresses = new ConcurrentHashMap<>();
    private Map<CustomerNumber, Address> defaultBillingAddresses = new ConcurrentHashMap<CustomerNumber, Address>();
    private Map<CustomerNumber, Address> defaultDeliveryAddresses = new ConcurrentHashMap<CustomerNumber, Address>();

    @PostConstruct
    public void initializeAddresses() {
        addAddress(new CustomerNumber("0815"), new Address(new AddressStreet("Poststr."), new AddressHouseNumber("1"), new AddressZipCode("26122"), new AddressCity("Oldenburg")));
        addAddress(new CustomerNumber("0815"), new Address(new AddressStreet("II. Hagen"), new AddressHouseNumber("7"), new AddressZipCode("45127"), new AddressCity("Essen")));
    }

    public List<Address> getAddresses(CustomerNumber customerNumber) {
        return new ArrayList<>(addresses.getOrDefault(customerNumber, emptySet()));
    }

    public void addAddress(CustomerNumber customerNumber, Address address) {
        Set<Address> customerAddresses = addresses.computeIfAbsent(customerNumber, number -> new LinkedHashSet<>());
        customerAddresses.add(address);
        defaultBillingAddresses.putIfAbsent(customerNumber, address);
        defaultDeliveryAddresses.putIfAbsent(customerNumber, address);
    }

    public Optional<Address> getDefaultBillingAddress(CustomerNumber customerNumber) {
        return ofNullable(defaultBillingAddresses.get(customerNumber));
    }

    public Optional<Address> getDefaultDeliveryAddress(CustomerNumber customerNumber) {
        return ofNullable(defaultDeliveryAddresses.get(customerNumber));
    }
}
