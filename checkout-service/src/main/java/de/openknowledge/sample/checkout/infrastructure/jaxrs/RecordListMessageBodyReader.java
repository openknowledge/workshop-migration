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
package de.openknowledge.sample.checkout.infrastructure.jaxrs;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import de.openknowledge.sample.checkout.infrastructure.common.AbstractRecordConverter;

/**
 * A class to read lists of records from jaxrs json messages.
 */
@Provider
@ApplicationScoped
@Consumes({APPLICATION_JSON, APPLICATION_FORM_URLENCODED, MULTIPART_FORM_DATA})
public class RecordListMessageBodyReader extends AbstractRecordConverter implements MessageBodyReader<List<? extends Record>> {

    @Context
    private Providers providers;
    @Inject
    private Instance<Validator> validatorInstance;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return List.class.equals(type)
            && hasRecordTypeArgument(genericType)
            && (mediaType.isCompatible(APPLICATION_JSON_TYPE)
                || stream(annotations).map(Annotation::annotationType).anyMatch(BeanParam.class::equals));
    }

    private boolean hasRecordTypeArgument(Type genericType) {
        return (genericType instanceof ParameterizedType)
            && (((ParameterizedType)genericType).getActualTypeArguments()[0] instanceof Class)
            && ((Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0]).isRecord();
    }

    @Override
    public List<? extends Record> readFrom(
        Class<List<? extends Record>> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream) throws IOException, WebApplicationException {

        Type genericList = new GenericType<List<Map>>() { }.getType();
        MessageBodyReader<List> jsonReader = providers.getMessageBodyReader(List.class, genericList, annotations, mediaType);
        List<?> value = jsonReader.readFrom(List.class, genericList, annotations, mediaType, httpHeaders, entityStream);
        Set<ConstraintViolation<?>> violations = null;
        if (ofNullable(validatorInstance).map(Instance::isResolvable).orElse(false)) {
            violations = new HashSet<>();
        }
        List<? extends Record> result = type.cast(instantiate(genericType, value, violations));
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
