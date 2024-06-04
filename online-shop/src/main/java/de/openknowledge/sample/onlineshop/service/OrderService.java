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
package de.openknowledge.sample.onlineshop.service;

import static de.openknowledge.sample.onlineshop.domain.order.OrderStatus.ORDERED;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.order.OrderAggregate;
import de.openknowledge.sample.onlineshop.domain.order.OrderNumber;
import de.openknowledge.sample.onlineshop.domain.order.OrderSummary;
import de.openknowledge.sample.onlineshop.domain.payment.CreditCardPayment;
import de.openknowledge.sample.onlineshop.domain.payment.DirectBillingPayment;
import de.openknowledge.sample.onlineshop.domain.payment.EmailPayment;
import de.openknowledge.sample.onlineshop.domain.shoppingcart.ShoppingCart;
import de.openknowledge.sample.onlineshop.infrastructure.jpa.Transactional;
import de.openknowledge.sample.onlineshop.repository.OrderRepository;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository repository;

    @Transactional
    public OrderNumber createOffer(ShoppingCart shoppingCart) {
        OrderAggregate order = repository.findByCustomer(shoppingCart.customerNumber());
        order.offerShoppingCart(shoppingCart);
        return order.getOrderNumber();
    }

    @Transactional
    public void setPayment(OrderNumber number, DirectBillingPayment directBillingPayment) {
        OrderAggregate aggregate = repository.findByOrderNumber(number);
        aggregate.setPayment(directBillingPayment);
    }

    @Transactional
    public void setPayment(OrderNumber number, CreditCardPayment creditCardPayment) {
        OrderAggregate aggregate = repository.findByOrderNumber(number);
        aggregate.setPayment(creditCardPayment);
    }

    @Transactional
    public void setPayment(OrderNumber number, EmailPayment emailPayment) {
        OrderAggregate aggregate = repository.findByOrderNumber(number);
        aggregate.setPayment(emailPayment);
    }

    @Transactional
    public void setBillingAddress(OrderNumber number, Address address) {
        OrderAggregate aggregate = repository.findByOrderNumber(number);
        aggregate.setBillingAddress(address);
    }

    @Transactional
    public void setDeliveryAddress(OrderNumber number, boolean sameAsBillingAddress, Address address) {
        OrderAggregate aggregate = repository.findByOrderNumber(number);
        if (sameAsBillingAddress) {
            aggregate.useBillingAddressAsDeliveryAddress();
        } else {
            aggregate.setDeliveryAddress(address);
        }
    }

    @Transactional
    public OrderSummary createSummary(OrderNumber offerNumber) {
        OrderAggregate order = repository.findByOrderNumber(offerNumber);
        return order.toSummary();
    }

    @Transactional
    public void order(OrderNumber number) {
		OrderSummary summary = createSummary(number);
		order(number, summary);
	}

    @Transactional
	public OrderNumber order(OrderSummary summary) {
		OrderAggregate order = repository.findByCustomer(summary.customerNumber());
		return order(order.getOrderNumber(), summary);
	}

    @Transactional
    public OrderNumber order(OrderNumber orderNumber, OrderSummary summary) {
        OrderAggregate order = repository.findByOrderNumber(orderNumber);
		order.update(summary);
    	order.setStatus(ORDERED);
		return order.getOrderNumber();
	}

	@Transactional
    public void createOrder(OrderNumber offerNumber) {
        OrderAggregate order = repository.findByOrderNumber(offerNumber);
        order.setStatus(ORDERED);
    }
}
