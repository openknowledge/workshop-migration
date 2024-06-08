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
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class OfferRepository {

    @Inject
    private EntityManager entityManager;

    public void persist(OfferAggregate aggregate) {
        entityManager.persist(aggregate);
    }

    public OfferAggregate find(OfferNumber number) {
        return entityManager.createNamedQuery(OfferAggregate.FIND_BY_OFFER_NUMBER, OfferAggregate.class)
                .setParameter("number", number.number())
                .getSingleResult();
    }
}
