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
import static org.apache.commons.lang3.Validate.matchesPattern;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DirectBillingPayment
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:36:32.859193Z[Etc/UTC]")
public record DirectBillingPayment(@NotNull @Valid DirectBillingPaymentName name,
        @NotNull @Valid DirectBillingPaymentIban iban, @NotNull @Valid DirectBillingPaymentBic bic) implements Payment {
    public DirectBillingPayment {
        requireNonNull(name);
        requireNonNull(iban);
        requireNonNull(bic);
    }
}

record DirectBillingPaymentName(@NotNull @Size(min = 2) String name) {
    DirectBillingPaymentName {
        requireNonNull(name);

        inclusiveBetween(name.length(), 2, Integer.MAX_VALUE);

    }
}

record DirectBillingPaymentIban(@NotNull @Pattern(regexp = "[A-Z]{2}\\d{16}\\d{16}*") String iban) {
    DirectBillingPaymentIban {
        requireNonNull(iban);

        matchesPattern(iban, "[A-Z]{2}\\d{16}\\d{16}*");

    }
}

record DirectBillingPaymentBic(@NotNull @Pattern(regexp = "[A-Z0-9]{8}[A-Z0-9]{3}*") String bic) {
    DirectBillingPaymentBic {
        requireNonNull(bic);

        matchesPattern(bic, "[A-Z0-9]{8}[A-Z0-9]{3}*");

    }
}
