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
package de.openknowledge.sample.onlineshop.infrastructure.jaxrs;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import de.openknowledge.sample.onlineshop.infrastructure.common.AbstractRecordConverter;

/**
 * A class to read lists of records from jaxrs json messages.
 */
@Provider
@ApplicationScoped
@Consumes(APPLICATION_JSON)
public class RecordMessageBodyReader extends AbstractRecordConverter implements MessageBodyReader<Record> {

    @Context
    private Providers providers;
    @Inject
    private Instance<Validator> validatorInstance;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isRecord(); 
    }

    @Override
    public Record readFrom(Class<Record> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        MessageBodyReader<Map> jsonReader = providers.getMessageBodyReader(Map.class, Map.class, annotations, mediaType);
        Map<String, Object> value = jsonReader.readFrom(Map.class, Map.class, annotations, mediaType, httpHeaders, entityStream);
        System.out.println(value);
        Set<ConstraintViolation<?>> violations = null;
        if (stream(annotations).map(Annotation::annotationType).anyMatch(Valid.class::equals)) {
            violations = new HashSet<>();
        }
        Record result = type.cast(instantiate(type, value::get, violations));
        if (ofNullable(violations).map(v -> !v.isEmpty()).orElse(false)) {
            throw new ConstraintViolationException(violations);
        }
        return result;
    }

    @Override
    protected Validator validator() {
        return validatorInstance.get();
    }
}
