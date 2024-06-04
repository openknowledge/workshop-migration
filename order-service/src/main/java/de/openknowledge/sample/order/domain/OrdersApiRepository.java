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
package de.openknowledge.sample.order.domain;

import static java.util.Collections.emptyList;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrdersApiRepository {

    private static final Random RANDOM = new Random();
    private static final Order EMTPY_ORDER = new Order(
            new OrderCustomerNumber("0"),
            emptyList(),
            new EmailPayment(new EmailPaymentEmail("max.mustermann@openknowledge.de")),
            new Address(new AddressStreet("Poststr."), new AddressHouseNumber("1"), new AddressZipCode("26122"), new AddressCity("Oldenburg")),
            new Address(new AddressStreet("Poststr."), new AddressHouseNumber("1"), new AddressZipCode("26122"), new AddressCity("Oldenburg")));
    private Map<OrderNumber, Order> orders = new ConcurrentHashMap<>();

    public OrderNumber createOrder(Order order) {
        OrderNumber number = new OrderNumber(Integer.toString(RANDOM.nextInt(10000)));
        orders.put(number, order != null ? order: EMTPY_ORDER);
        return number;
    }

    public void updateOrder(OrderNumber orderNumber, Order order) {
        Order old = orders.get(orderNumber);
        if (old == null) {
            throw new IllegalArgumentException("Order with number " + orderNumber.orderNumber() + " does not exist");
        }
        if (old == EMTPY_ORDER) { // intentionally using pointer comparison
            orders.put(orderNumber, order);
        } else {
            // check that order was not changed
            if (!old.equals(order)) {
                throw new IllegalArgumentException("Order may not be changed");
            }
            // else ignore call
        }
    }
}
