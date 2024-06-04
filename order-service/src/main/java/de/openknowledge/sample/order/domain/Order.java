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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Order
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:36:32.859193Z[Etc/UTC]")
public record Order (
  @NotNull @Valid
  OrderCustomerNumber customerNumber,
  @NotNull @Valid
  List<Item> items,
  @NotNull @Valid
  Payment payment,
  @NotNull @Valid
  Address billingAddress,
  @NotNull @Valid
  Address deliveryAddress
  )
   {
  public Order {
      requireNonNull(customerNumber);
      requireNonNull(items);
      requireNonNull(payment);
      requireNonNull(billingAddress);
      requireNonNull(deliveryAddress);
  }
}
