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

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.isTrue;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.cart.domain.Quantity.Adapter;
import de.openknowledge.sample.cart.infrastructure.jsonb.AbstractRecordValueTypeAdapter;

@JsonbTypeAdapter(Adapter.class)
public record Quantity(@NotNull @Min(1) Integer quantity) {
    public Quantity {
        requireNonNull(quantity);
        isTrue(quantity.intValue() >= 1);
    }

    public Quantity(String quantity) {
        this(Integer.valueOf(quantity));
    }


    public static class Adapter extends AbstractRecordValueTypeAdapter<Quantity, Integer> {
 
    }

}
