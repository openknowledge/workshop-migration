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
package de.openknowledge.sample.checkout.application;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import de.openknowledge.sample.checkout.domain.OfferNumber;
import de.openknowledge.sample.checkout.domain.customer.CustomerNumber;
import de.openknowledge.sample.checkout.domain.offer.OfferRepository;

@Named("offer")
@RequestScoped
public class OfferBean {

    @Inject
    private OfferRepository repository;

    private OfferNumber number;
    private CustomerNumber customerNumber;

    public OfferNumber getNumber() {
        return number;
    }

    public void setNumber(OfferNumber number) {
        this.number = number;
        this.customerNumber = repository.find(number).getCustomerNumber();
    }

    @Produces
    public CustomerNumber getCustomerNumber() {
        return customerNumber;
    }
}
