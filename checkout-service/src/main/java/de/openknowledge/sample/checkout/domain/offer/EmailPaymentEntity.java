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

import de.openknowledge.sample.checkout.domain.offer.payment.EmailPayment;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_EMAIL_PAYMENT")
public class EmailPaymentEntity extends AbstractPaymentEntity<EmailPayment> {

    @Embedded
    @AttributeOverride(name = "email.address", column = @Column(name = "PAY_EMAIL_ADDRESS"))
    private EmailPayment emailPayment;

    protected EmailPaymentEntity() {
        // for frameworks
    }

    public EmailPaymentEntity(EmailPayment emailPayment) {
        this.emailPayment = requireNonNull(emailPayment);
    }

    public EmailPayment get() {
        return emailPayment;
    }
}
