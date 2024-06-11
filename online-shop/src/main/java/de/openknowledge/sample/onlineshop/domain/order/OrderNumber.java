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
package de.openknowledge.sample.onlineshop.domain.order;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.matchesPattern;

import java.util.Random;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record OrderNumber(@NotNull @Pattern(regexp = "\\d+") String number) {

    private static final int MAX_ORDER_NUMBER = 10000;
    private static final Random RANDOM = new Random();

    public OrderNumber {
        requireNonNull(number);
        matchesPattern(number, "\\d+");
    }

    public OrderNumber() {
        this(Integer.toString(RANDOM.nextInt(MAX_ORDER_NUMBER)));
    }
}
