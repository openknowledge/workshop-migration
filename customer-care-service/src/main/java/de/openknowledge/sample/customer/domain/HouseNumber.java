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
package de.openknowledge.sample.customer.domain;

import static org.apache.commons.lang3.Validate.inclusiveBetween;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.Size;

import de.openknowledge.sample.customer.domain.HouseNumber.HouseNumberAdapter;
import de.openknowledge.sample.customer.infrastructure.jsonb.AbstractRecordValueTypeAdapter;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonbTypeAdapter(HouseNumberAdapter.class)
public record HouseNumber(@Size(min = 1) String number) {
    public HouseNumber {
        if (number != null) {
            inclusiveBetween(1, Integer.MAX_VALUE, number.length());
        }
    }

    public static class HouseNumberAdapter extends AbstractRecordValueTypeAdapter<HouseNumber, String> {
    }
}
