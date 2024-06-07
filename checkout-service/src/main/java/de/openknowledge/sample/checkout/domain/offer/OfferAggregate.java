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

import static de.openknowledge.sample.checkout.domain.offer.OfferAggregate.FIND_BY_OFFER_NUMBER;
import static jakarta.persistence.CascadeType.PERSIST;
import static java.util.Objects.requireNonNull;

import java.util.List;

import de.openknowledge.sample.checkout.domain.OfferNumber;
import de.openknowledge.sample.checkout.domain.address.Address;
import de.openknowledge.sample.checkout.domain.cart.Item;
import de.openknowledge.sample.checkout.domain.customer.CustomerNumber;
import de.openknowledge.sample.checkout.domain.offer.payment.CreditCardPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.DirectBillingPayment;
import de.openknowledge.sample.checkout.domain.offer.payment.EmailPayment;
import de.openknowledge.sample.checkout.domain.order.Order;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_OFFER")
@NamedQuery(name = FIND_BY_OFFER_NUMBER, query = "SELECT o FROM OfferAggregate o WHERE o.offerNumber.number = :number")
public class OfferAggregate {

    public static final String FIND_BY_OFFER_NUMBER = "OfferAggregate.findByOfferNumber";

    @Id
    @SequenceGenerator(name = "SEQ_OFFER")
    @GeneratedValue(generator = "SEQ_OFFER")
    @Column(name = "OFF_ID")
    private long id;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "OFF_CUSTOMER_NUMBER"))
    private CustomerNumber customerNumber;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "OFF_OFFER_NUMBER"))
    private OfferNumber offerNumber;

    @ElementCollection
    @CollectionTable(name = "TAB_OFFER_ITEM", joinColumns = @JoinColumn(name = "OFI_OFF_ID"))
    @AttributeOverride(name = "productNumber.number", column = @Column(name = "OFI_PRODUCT_NUMBER"))
    @AttributeOverride(name = "quantity.quantity", column = @Column(name = "OFI_QUANTITY"))
    private List<Item> items;

    @OneToOne(cascade = PERSIST, orphanRemoval = true)
    @JoinColumn(name = "OFF_PAY_ID")
    private AbstractPaymentEntity<?> payment;

    @Embedded
    @AttributeOverride(name = "street.name", column = @Column(name = "OFF_BILLING_ADDRESS_STREET"))
    @AttributeOverride(name = "houseNumber.number", column = @Column(name = "OFF_BILLING_ADDRESS_HOUSE_NUMBER"))
    @AttributeOverride(name = "zipCode.code", column = @Column(name = "OFF_BILLING_ADDRESS_ZIP_CODE"))
    @AttributeOverride(name = "city.name", column = @Column(name = "OFF_BILLING_ADDRESS_CITY"))
    private Address billingAddress;

    @Embedded
    @AttributeOverride(name = "street.name", column = @Column(name = "OFF_DELIVERY_ADDRESS_STREET"))
    @AttributeOverride(name = "houseNumber.number", column = @Column(name = "OFF_DELIVERY_ADDRESS_HOUSE_NUMBER"))
    @AttributeOverride(name = "zipCode.code", column = @Column(name = "OFF_DELIVERY_ADDRESS_ZIP_CODE"))
    @AttributeOverride(name = "city.name", column = @Column(name = "OFF_DELIVERY_ADDRESS_CITY"))
    private Address deliveryAddress;

    protected OfferAggregate() {
        // for frameworks
    }

    public OfferAggregate(Offer offer) {
        this.offerNumber = new OfferNumber();
        this.customerNumber = offer.customerNumber();
        this.items = offer.items();
    }

    public CustomerNumber getCustomerNumber() {
        return customerNumber;
    }

    public OfferNumber getOfferNumber() {
        return offerNumber;
    }

    public void setPayment(DirectBillingPayment directBillingPayment) {
        payment = new DirectBillingPaymentEntity(directBillingPayment);
    }

    public void setPayment(CreditCardPayment creditCardPayment) {
        payment = new CreditCardPaymentEntity(creditCardPayment);
    }

    public void setPayment(EmailPayment emailPayment) {
        payment = new EmailPaymentEntity(emailPayment);
    }

    public void setBillingAddress(Address address) {
        this.billingAddress = requireNonNull(address);
    }

    public void useBillingAddressAsDeliveryAddress() {
        this.deliveryAddress = billingAddress;
    }

    public void setDeliveryAddress(Address address) {
        this.deliveryAddress = requireNonNull(address);
    }

    public Order toOrder() {
        return new Order(customerNumber, items, payment.get(), billingAddress, deliveryAddress);
    }
}
