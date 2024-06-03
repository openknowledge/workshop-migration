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
import javax.inject.Named;

import de.openknowledge.sample.checkout.domain.order.Order;

@Named("summary")
@RequestScoped
public class OrderBean {

    private Order order;
    
    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        System.out.println(order);
        this.order = order;
    }
}
