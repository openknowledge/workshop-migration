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
package de.openknowledge.sample.customer.application;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import de.openknowledge.sample.customer.domain.Address;
import de.openknowledge.sample.customer.domain.CustomerAggregate;
import de.openknowledge.sample.customer.domain.CustomerNumber;

@Named("customer")
@RequestScoped
public class CustomerBean {

	private CustomerAggregate customer;

	public CustomerNumber getNumber() {
		return customer.getCustomerNumber();
	}

	public void setCustomer(CustomerAggregate customer) {
		this.customer = customer;
	}

	public Address getDefaultBillingAddress() {
		return customer.getDefaultBillingAddress().orElse(null);
	}

	public Address getDefaultDeliveryAddress() {
		return customer.getDefaultDeliveryAddress().orElse(null);
	}

	public List<Address> getAddresses() {
		return customer.getAddresses();
	}
}
