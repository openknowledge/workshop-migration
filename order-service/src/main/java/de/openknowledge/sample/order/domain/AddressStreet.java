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

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.openknowledge.sample.order.domain.AddressStreet.StreetAdapter;
import de.openknowledge.sample.order.infrastructure.jsonb.AbstractRecordValueTypeAdapter;

@JsonbTypeAdapter(StreetAdapter.class)
public record AddressStreet(@NotNull @Size(min = 2) String street) {
    public AddressStreet {
        requireNonNull(street);
        inclusiveBetween(2, Integer.MAX_VALUE, street.length());
    }

    public static class StreetAdapter extends AbstractRecordValueTypeAdapter<AddressStreet, String> {
    }
}
