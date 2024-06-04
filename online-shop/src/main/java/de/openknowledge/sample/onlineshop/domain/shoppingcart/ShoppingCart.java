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
package de.openknowledge.sample.onlineshop.domain.shoppingcart;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.onlineshop.domain.customer.CustomerNumber;
import de.openknowledge.sample.onlineshop.domain.order.Item;
import de.openknowledge.sample.onlineshop.domain.order.ProductNumber;
import de.openknowledge.sample.onlineshop.domain.order.Quantity;

public record ShoppingCart(@NotNull @Valid CustomerNumber customerNumber, @NotNull @Valid List<Item> items) {
    public ShoppingCart {
        requireNonNull(customerNumber);
        requireNonNull(items);
    }

    public ShoppingCart(CustomerNumber customerNumber, List<ProductNumber> productNumbers, List<Quantity> quantities) {
        this(customerNumber, createItems(productNumbers, quantities));
    }

    private static List<Item> createItems(List<ProductNumber> productNumbers, List<Quantity> quantities) {
        if (productNumbers.size() != quantities.size()) {
            throw new IllegalArgumentException("product numbers size " + productNumbers.size()
                    + " does not match quantities size " + quantities.size());
        }
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < productNumbers.size(); i++) {
            items.add(new Item(productNumbers.get(i), quantities.get(i)));
        }
        return unmodifiableList(items);
    }
}
