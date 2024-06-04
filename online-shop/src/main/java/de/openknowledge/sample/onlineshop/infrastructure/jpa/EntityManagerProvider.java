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
package de.openknowledge.sample.onlineshop.infrastructure.jpa;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProvider {

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        Consumer<String> setUrl = url -> properties.put("jakarta.persistence.jdbc.url", url);
        Consumer<String> setUser = user -> properties.put("jakarta.persistence.jdbc.user", user);
        Consumer<String> setPassword = password -> properties.put("jakarta.persistence.jdbc.password", password);
        ofNullable(getProperty("jakarta.persistence.jdbc.url")).ifPresent(setUrl);
        ofNullable(getProperty("jakarta.persistence.jdbc.user")).ifPresent(setUser);
        ofNullable(getProperty("jakarta.persistence.jdbc.password")).ifPresent(setPassword);
        ofNullable(getenv("JAKARTA_PERSISTENCE_JDBC_URL")).ifPresent(setUrl);
        ofNullable(getenv("JAKARTA_PERSISTENCE_JDBC_USER")).ifPresent(setUser);
        ofNullable(getenv("JAKARTA_PERSISTENCE_JDBC_PASSWORD")).ifPresent(setPassword);
        return Persistence.createEntityManagerFactory("onlineshop", properties);
    }

    public void closeEntityManagerFactory(@Disposes EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager entityManager) {
        entityManager.close();
    }
}
