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
package de.openknowledge.sample.onlineshop.domain.address;

import static java.util.Objects.requireNonNull;

import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_ADDRESS")
public class AddressEntity {

    @Id
    @SequenceGenerator(name = "SEQ_ADDRESS")
    @GeneratedValue(generator = "SEQ_ADDRESS")
    @Column(name = "ADR_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ADR_CUS_ID")
    private CustomerAggregate customer;

    @Embedded
    @AttributeOverride(name = "street.name", column = @Column(name = "ADR_STREET"))
    @AttributeOverride(name = "houseNumber.number", column = @Column(name = "ADR_HOUSE_NUMBER"))
    @AttributeOverride(name = "zipCode.code", column = @Column(name = "ADR_ZIP_CODE"))
    @AttributeOverride(name = "city.name", column = @Column(name = "ADR_CITY"))
    private Address address;

    protected AddressEntity() {
        // for frameworks
    }

    public AddressEntity(CustomerAggregate customer, Address address) {
    	this.customer = requireNonNull(customer);
    	this.address = requireNonNull(address);
    }

    public Address getAddress() {
    	return address;
    }
}
