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
package de.openknowledge.sample.onlineshop.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class CustomerRepository {

	@Inject
	private EntityManager entityManager;

	public Optional<CustomerAggregate> findCustomer(CustomerNumber customerNumber) {
        return entityManager.createNamedQuery(CustomerAggregate.FIND_BY_CUSTOMER_NUMBER, CustomerAggregate.class)
            .setParameter("number", customerNumber.number())
            .getResultStream()
            .findAny();
	}
}
