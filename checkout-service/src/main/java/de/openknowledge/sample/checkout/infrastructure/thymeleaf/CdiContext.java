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
package de.openknowledge.sample.checkout.infrastructure.thymeleaf;

import static java.util.stream.Collectors.toSet;

import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.IContext;

@RequestScoped
public class CdiContext implements IContext {

    @Inject
    private Instance<Object> instance;
    
    private Locale locale;
    private Set<String> beanNames;

    @Inject
    public void initialize(HttpServletRequest request, BeanManager beanManager) {
        locale = request.getLocale();
        beanNames = beanManager.getBeans(Object.class, Any.Literal.INSTANCE)
                .stream()
                .flatMap(bean -> bean.getQualifiers().stream().filter(qualifier -> qualifier.annotationType().equals(Named.class)))
                .map(named -> Named.class.cast(named).value())
                .collect(toSet());
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean containsVariable(String name) {
        return beanNames.contains(name);
    }

    @Override
    public Set<String> getVariableNames() {
        return beanNames;
    }

    @Override
    public Object getVariable(String name) {
        return instance.select(NamedLiteral.of(name)).get();
    }
}
