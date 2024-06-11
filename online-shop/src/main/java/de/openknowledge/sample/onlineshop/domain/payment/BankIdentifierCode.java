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
package de.openknowledge.sample.onlineshop.domain.payment;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.matchesPattern;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import de.openknowledge.sample.onlineshop.domain.payment.BankIdentifierCode.Adapter;
import de.openknowledge.sample.onlineshop.infrastructure.jsonb.AbstractRecordValueTypeAdapter;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonbTypeAdapter(Adapter.class)
public record BankIdentifierCode(@NotNull @Pattern(regexp = "[A-Z0-9]{8,11}") String code) {
    public BankIdentifierCode {
        requireNonNull(code);
        matchesPattern(code, "[A-Z0-9]{8,11}");
    }

    public static class Adapter extends AbstractRecordValueTypeAdapter<BankIdentifierCode, String> {

    }
}
