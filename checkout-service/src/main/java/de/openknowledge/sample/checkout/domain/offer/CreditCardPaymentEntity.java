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
package de.openknowledge.sample.checkout.domain.offer;

import static java.util.Objects.requireNonNull;

import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardPayment;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_CREDIT_CARD_PAYMENT")
public class CreditCardPaymentEntity extends AbstractPaymentEntity<CreditCardPayment> {

    @Embedded
    @AttributeOverride(name = "name.name", column = @Column(name = "PAY_NAME"))
    @AttributeOverride(name = "number.number", column = @Column(name = "PAY_NUMBER"))
    @AttributeOverride(name = "expiryMonth.month", column = @Column(name = "PAY_EXPIRY_MONTH"))
    @AttributeOverride(name = "expiryYear.year", column = @Column(name = "PAY_EXPIRY_YEAR"))
    @AttributeOverride(name = "cvn.number", column = @Column(name = "PAY_CARD_VERIFICATION_NUMBER"))
    private CreditCardPayment creditCard;

    protected CreditCardPaymentEntity() {
        // for frameworks
    }

    public CreditCardPaymentEntity(CreditCardPayment creditCard) {
        this.creditCard = requireNonNull(creditCard);
    }

    public CreditCardPayment get() {
        return creditCard;
    }
}
