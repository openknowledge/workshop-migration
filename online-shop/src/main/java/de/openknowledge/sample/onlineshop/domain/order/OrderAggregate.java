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
package de.openknowledge.sample.onlineshop.domain.order;

import static de.openknowledge.sample.onlineshop.domain.order.OrderAggregate.FIND_BY_CUSTOMER_NUMBER;
import static de.openknowledge.sample.onlineshop.domain.order.OrderAggregate.FIND_BY_ORDER_NUMBER;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.requireNonNull;

import java.util.List;

import de.openknowledge.sample.onlineshop.domain.address.Address;
import de.openknowledge.sample.onlineshop.domain.address.AddressEntity;
import de.openknowledge.sample.onlineshop.domain.customer.CustomerAggregate;
import de.openknowledge.sample.onlineshop.domain.payment.AbstractPaymentEntity;
import de.openknowledge.sample.onlineshop.domain.payment.CreditCardPayment;
import de.openknowledge.sample.onlineshop.domain.payment.CreditCardPaymentEntity;
import de.openknowledge.sample.onlineshop.domain.payment.DirectBillingPayment;
import de.openknowledge.sample.onlineshop.domain.payment.DirectBillingPaymentEntity;
import de.openknowledge.sample.onlineshop.domain.payment.EmailPayment;
import de.openknowledge.sample.onlineshop.domain.payment.EmailPaymentEntity;
import de.openknowledge.sample.onlineshop.domain.shoppingcart.ShoppingCart;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAB_ORDER")
@NamedQuery(name = FIND_BY_ORDER_NUMBER, query = "SELECT o FROM OrderAggregate o WHERE o.orderNumber.number = :number")
@NamedQuery(name = FIND_BY_CUSTOMER_NUMBER, query = "SELECT o FROM OrderAggregate o WHERE o.customer.customerNumber.number = :number")
public class OrderAggregate {

    public static final String FIND_BY_ORDER_NUMBER = "OrderAggregate.findByOrderNumber";
    public static final String FIND_BY_CUSTOMER_NUMBER = "OrderAggregate.findByCustomerNumber";

    @Id
    @SequenceGenerator(name = "SEQ_ORDER")
    @GeneratedValue(generator = "SEQ_ORDER")
    @Column(name = "ORD_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ORD_CUS_ID")
    private CustomerAggregate customer;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "ORD_ORDER_NUMBER"))
    private OrderNumber orderNumber;

    @Enumerated(STRING)
    @Column(name = "ORD_STATUS")
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "TAB_ORDER_ITEM", joinColumns = @JoinColumn(name = "ORI_ORD_ID"))
    @AttributeOverride(name = "productNumber.number", column = @Column(name = "ORI_PRODUCT_NUMBER"))
    @AttributeOverride(name = "quantity.quantity", column = @Column(name = "ORI_QUANTITY"))
    private List<Item> items;

    @OneToOne(cascade = PERSIST, orphanRemoval = true)
    @JoinColumn(name = "ORD_PAY_ID")
    private AbstractPaymentEntity<?> payment;

    @ManyToOne(cascade = PERSIST)
    @JoinColumn(name = "ORD_BILLING_ADR_ID")
    private AddressEntity billingAddress;

    @ManyToOne(cascade = PERSIST)
    @JoinColumn(name = "ORD_DELIVERY_ADR_ID")
    private AddressEntity deliveryAddress;

    protected OrderAggregate() {
        // for frameworks
    }

    public OrderAggregate(CustomerAggregate customer, OrderStatus status) {
        this.orderNumber = new OrderNumber();
        this.customer = requireNonNull(customer);
        this.status = requireNonNull(status);
    }

    public void offerShoppingCart(ShoppingCart shoppingCart) {
        this.items = shoppingCart.items();
        this.status = OrderStatus.OFFERED;
    }

    public CustomerAggregate getCustomer() {
        return customer;
    }

    public OrderNumber getOrderNumber() {
        return orderNumber;
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
        this.billingAddress = customer.getAddress(address).orElseGet(() -> {
        	customer.addAddress(address);
        	return customer.getAddress(address).get();
        });
    }

    public void useBillingAddressAsDeliveryAddress() {
        this.deliveryAddress = billingAddress;
    }

    public void setDeliveryAddress(Address address) {
        this.deliveryAddress = customer.getAddress(address).orElseGet(() -> {
        	customer.addAddress(address);
        	return customer.getAddress(address).get();
        });
    }

	public void setStatus(OrderStatus status) {
		this.status = requireNonNull(status);
	}

	public void update(OrderSummary summary) {
		if (!summary.customerNumber().equals(customer.getCustomerNumber())) {
			throw new IllegalArgumentException("Changing the customer is not allowed");
		}
		setBillingAddress(summary.billingAddress());
		setDeliveryAddress(summary.deliveryAddress());
		if (summary.isCreditCardPayment()) {
			setPayment((CreditCardPayment)summary.payment());
		} else if (summary.isDirectBillingPayment()) {
			setPayment((DirectBillingPayment)summary.payment());
		} else {
			setPayment((EmailPayment)summary.payment());
		}
		this.items = summary.items();
	}

	public OrderSummary toSummary() {
        return new OrderSummary(customer.getCustomerNumber(), items, payment.get(), billingAddress.getAddress(), deliveryAddress.getAddress());
    }
}
