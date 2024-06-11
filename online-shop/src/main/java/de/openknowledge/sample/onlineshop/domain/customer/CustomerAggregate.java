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
package de.openknowledge.sample.onlineshop.domain.customer;

import static de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate.FIND_BY_CUSTOMER_NUMBER;
import static jakarta.persistence.CascadeType.PERSIST;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.address.AddressEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_CUSTOMER")
@NamedQuery(name = FIND_BY_CUSTOMER_NUMBER, query = "SELECT c FROM CustomerAggregate c WHERE c.customerNumber.number = :number")
public class CustomerAggregate {

    public static final String FIND_BY_CUSTOMER_NUMBER = "CustomerAggregate.findByCustomerNumber";

    @Id
    @SequenceGenerator(name = "SEQ_CUSTOMER")
    @GeneratedValue(generator = "SEQ_CUSTOMER")
    @Column(name = "CUS_ID")
    private long id;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "CUS_CUSTOMER_NUMBER"))
    private CustomerNumber customerNumber;

    @OrderBy
    @OneToMany(mappedBy = "customer")
    private Set<AddressEntity> addresses;

    @OneToOne(cascade = PERSIST, orphanRemoval = true)
    @JoinColumn(name = "CUS_DEFAULT_BILLING_ADR_ID")
    private AddressEntity defaultBillingAddress;

    @OneToOne(cascade = PERSIST, orphanRemoval = true)
    @JoinColumn(name = "CUS_DEFAULT_DELIVERY_ADR_ID")
    private AddressEntity defaultDeliveryAddress;

    protected CustomerAggregate() {
        // for frameworks
    }

    public CustomerAggregate(CustomerNumber customerNumber) {
        this.customerNumber = customerNumber;
    }

    public CustomerNumber getCustomerNumber() {
        return customerNumber;
    }

    public Optional<Address> getDefaultBillingAddress() {
        return ofNullable(defaultBillingAddress).map(AddressEntity::getAddress);
    }

    public void setDefaultBillingAddress(Address address) {
        addAddress(address);
        this.defaultBillingAddress = getAddress(address).orElseThrow(UnsupportedOperationException::new);
    }

    public Optional<Address> getDefaultDeliveryAddress() {
        return ofNullable(defaultDeliveryAddress).map(AddressEntity::getAddress);
    }

    public void setDefaultDeliveryAddress(Address address) {
        addAddress(address);
        this.defaultDeliveryAddress = getAddress(address).orElseThrow(UnsupportedOperationException::new);
    }

    public void addAddress(Address address) {
        if (addresses == null) {
            addresses = new LinkedHashSet<AddressEntity>();
            addresses.add(new AddressEntity(this, address));
        } else if (getAddress(address).isEmpty()) {
            addresses.add(new AddressEntity(this, address));
        }
    }

    public Optional<AddressEntity> getAddress(Address address) {
        if (addresses == null) {
            return Optional.empty();
        }
        return this.addresses.stream()
            .filter(entity -> entity.getAddress().equals(address))
            .findAny();
    }

    public List<Address> getAddresses() {
        if (addresses == null) {
            return emptyList();
        }
        return addresses.stream().map(AddressEntity::getAddress).toList();
    }
}
