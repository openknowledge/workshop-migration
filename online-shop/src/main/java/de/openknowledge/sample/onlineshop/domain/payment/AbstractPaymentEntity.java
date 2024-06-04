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

import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

import de.openknowledge.sample.onlineshop.domain.order.OrderAggregate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@Inheritance(strategy = TABLE_PER_CLASS)
public abstract class AbstractPaymentEntity<P extends Payment> {

    @Id
    @SequenceGenerator(name = "SEQ_PAYMENT")
    @GeneratedValue(generator = "SEQ_PAYMENT")
    @Column(name = "PAY_ID")
    private long id;
    @OneToOne
    @JoinColumn(name = "PAY_ORD_ID")
    private OrderAggregate order;

    public abstract P get();
}
