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
package de.openknowledge.sample.checkout.domain.offer.payment;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import de.openknowledge.sample.checkout.domain.offer.payment.Month.Adapter;
import de.openknowledge.sample.checkout.infrastructure.jsonb.AbstractRecordValueTypeAdapter;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonbTypeAdapter(Adapter.class)
public record Month(@NotNull @Min(1) @Max(12) Integer month) {
    public Month {
        requireNonNull(month);
        inclusiveBetween(1, 12, month.intValue());
    }

    public Month(String expiryMonth) {
        this(Integer.valueOf(expiryMonth));
    }

    public static class Adapter extends AbstractRecordValueTypeAdapter<Month, Integer> {
        
    }
}
