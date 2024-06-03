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
package de.openknowledge.sample.checkout.domain.offer;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.checkout.domain.cart.Item;
import de.openknowledge.sample.checkout.domain.customer.CustomerNumber;

public record Offer(@NotNull @Valid CustomerNumber customerNumber, @NotNull @Valid List<Item> items) {
    public Offer {
        requireNonNull(customerNumber);
        requireNonNull(items);
    }
}
