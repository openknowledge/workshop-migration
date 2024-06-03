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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.openknowledge.sample.checkout.domain.OfferNumber;
import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.DirectBillingPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.EmailPayment;
import de.openknowledge.sample.checkout.domain.order.Order;
import de.openknowledge.sample.checkout.infrastructure.jpa.Transactional;

@ApplicationScoped
public class OfferService {

    @Inject
    private OfferRepository repository;

    @Transactional
    public OfferNumber createOffer(Offer offer) {
        OfferAggregate aggregate = new OfferAggregate(offer);
        repository.persist(aggregate);
        return aggregate.getOfferNumber();
    }

    @Transactional
    public void setPayment(OfferNumber number, DirectBillingPayment directBillingPayment) {
        OfferAggregate aggregate = repository.find(number);
        aggregate.setPayment(directBillingPayment);
    }

    @Transactional
    public void setPayment(OfferNumber number, CreditCardPayment creditCardPayment) {
        OfferAggregate aggregate = repository.find(number);
        aggregate.setPayment(creditCardPayment);
    }

    @Transactional
    public void setPayment(OfferNumber number, EmailPayment emailPayment) {
        OfferAggregate aggregate = repository.find(number);
        aggregate.setPayment(emailPayment);
    }

    @Transactional
    public void setBillingAddress(OfferNumber number, Address address) {
        OfferAggregate aggregate = repository.find(number);
        aggregate.setBillingAddress(address);
    }

    @Transactional
    public void setDeliveryAddress(OfferNumber number, boolean sameAsBillingAddress, Address address) {
        OfferAggregate aggregate = repository.find(number);
        if (sameAsBillingAddress) {
            aggregate.useBillingAddressAsDeliveryAddress();
        } else {
            aggregate.setDeliveryAddress(address);
        }
    }

    @Transactional
    public Order createOrder(OfferNumber offerNumber) {
        OfferAggregate offer = repository.find(offerNumber);
        return offer.toOrder();
    }
}
