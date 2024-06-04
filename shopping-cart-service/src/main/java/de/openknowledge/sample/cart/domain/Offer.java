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
package de.openknowledge.sample.cart.domain;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Offer
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:39:44.713995Z[Etc/UTC]")
public record Offer(@NotNull @Valid CustomerNumber customerNumber, @NotNull @Valid List<Item> items) {
    public Offer {
        requireNonNull(customerNumber);
        requireNonNull(items);
    }

    public Offer(CustomerNumber customerNumber, List<ProductNumber> productNumbers, List<Quantity> quantities) {
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
