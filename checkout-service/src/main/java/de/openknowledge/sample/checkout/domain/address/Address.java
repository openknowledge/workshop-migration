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
package de.openknowledge.sample.checkout.domain.address;

import static java.util.Objects.requireNonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

public record Address(
        @FormParam("street") @NotNull @Valid Street street,
        @FormParam("houseNumber") @Valid HouseNumber houseNumber,
        @FormParam("zipCode") @NotNull @Valid ZipCode zipCode,
        @FormParam("city") @NotNull @Valid City city) {
    public Address {
        requireNonNull(street);
        requireNonNull(zipCode);
        requireNonNull(city);
    }
}
