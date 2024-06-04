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
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Item
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:36:32.859193Z[Etc/UTC]")
public record Item (
  @NotNull @Valid
  ItemProductNumber productNumber,
  @NotNull @Valid
  ItemQuantity quantity)
   {
  public Item {
      requireNonNull(productNumber);
      requireNonNull(quantity);
  }
}

record ItemProductNumber (@NotNull  @Pattern(regexp="\\d+")String productNumber) {
  ItemProductNumber {
    requireNonNull(productNumber);
    
matchesPattern(productNumber, "\\d+");



  }
}

record ItemQuantity (@NotNull  @Min(1)Integer quantity) {
  ItemQuantity {
    requireNonNull(quantity);
    
isTrue(quantity.intValue() >= 1);



  }
}

