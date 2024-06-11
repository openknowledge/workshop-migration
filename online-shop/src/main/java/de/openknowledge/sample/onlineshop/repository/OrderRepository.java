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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.domain.order.OrderAggregate;
import de.openknowledge.sample.onlineshop.domain.order.OrderNumber;
import de.openknowledge.sample.onlineshop.infrastructure.jpa.Transactional;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class OrderRepository {

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void persist(OrderAggregate aggregate) {
        entityManager.persist(aggregate);
    }

    public OrderAggregate findByCustomer(@NotNull @Valid CustomerNumber customerNumber) {
        return entityManager.createNamedQuery(OrderAggregate.FIND_BY_CUSTOMER_NUMBER, OrderAggregate.class)
                .setParameter("customerNumber", customerNumber.number())
                .getSingleResult();
    }

    public OrderAggregate findByOrderNumber(OrderNumber number) {
        return entityManager.createNamedQuery(OrderAggregate.FIND_BY_ORDER_NUMBER, OrderAggregate.class)
                .setParameter("orderNumber", number.number())
                .getSingleResult();
    }
}
