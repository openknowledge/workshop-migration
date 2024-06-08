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
package de.openknowledge.sample.checkout.domain.cart;

import static java.util.Objects.requireNonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record Item(@NotNull @Valid ProductNumber productNumber, @NotNull @Valid ItemQuantity quantity) {
    public Item {
        requireNonNull(productNumber);
        requireNonNull(quantity);
    }
}
