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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * CreditCardPayment
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2023-11-29T10:36:32.859193Z[Etc/UTC]")
public record CreditCardPayment (
  @NotNull @Valid
  CreditCardPaymentName name,
  @NotNull @Valid
  CreditCardPaymentNumber number,
  @NotNull @Valid
  CreditCardPaymentExpiryMonth expiryMonth,
  @NotNull @Valid
  CreditCardPaymentExpiryYear expiryYear,
  @NotNull @Valid
  CreditCardPaymentCvn cvn)
implements Payment {
  public CreditCardPayment {
      requireNonNull(name);
      requireNonNull(number);
      requireNonNull(expiryMonth);
      requireNonNull(expiryYear);
      requireNonNull(cvn);
  }
}

record CreditCardPaymentName (@NotNull  @Size(min=2)String name) {
  CreditCardPaymentName {
    requireNonNull(name);
    
inclusiveBetween(name.length(), 2, Integer.MAX_VALUE);



  }
}

record CreditCardPaymentNumber (@NotNull  @Pattern(regexp="\\d{16}")String number) {
  CreditCardPaymentNumber {
    requireNonNull(number);
    
matchesPattern(number, "\\d{16}");



  }
}

record CreditCardPaymentExpiryMonth (@NotNull  @Min(1) @Max(12)Integer expiryMonth) {
  CreditCardPaymentExpiryMonth {
    requireNonNull(expiryMonth);
    
inclusiveBetween(expiryMonth.intValue(), 1, 12);



  }
}

record CreditCardPaymentExpiryYear (@NotNull  @Min(1) @Max(12)Integer expiryYear) {
  CreditCardPaymentExpiryYear {
    requireNonNull(expiryYear);
    
inclusiveBetween(2023, 2035, expiryYear.intValue());



  }
}

record CreditCardPaymentCvn (@NotNull  @Pattern(regexp="\\d{3}")String cvn) {
  CreditCardPaymentCvn {
    requireNonNull(cvn);
    
matchesPattern(cvn, "\\d{3}");



  }
}

