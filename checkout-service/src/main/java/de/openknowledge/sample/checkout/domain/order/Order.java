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
package de.openknowledge.sample.checkout.domain.order;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.cart.Item;
import de.openknowledge.sample.checkout.domain.customer.CustomerNumber;
import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.DirectBillingPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.EmailPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.Payment;

public record Order(
    @NotNull @Valid CustomerNumber customerNumber,
    @NotNull @Valid List<Item> items,
    @NotNull @Valid Payment payment,
    @NotNull @Valid Address billingAddress,
    @NotNull @Valid Address deliveryAddress) {

    public Order {
        requireNonNull(customerNumber);
        requireNonNull(items);
        requireNonNull(payment);
        requireNonNull(billingAddress);
        requireNonNull(deliveryAddress);
    }

    @JsonbTransient
    public boolean isDirectBillingPayment() {
        return payment() instanceof DirectBillingPayment;
    }

    @JsonbTransient
    public boolean isCreditCardPayment() {
        return payment() instanceof CreditCardPayment;
    }

    @JsonbTransient
    public boolean isEmailPayment() {
        return payment() instanceof EmailPayment;
    }
}
