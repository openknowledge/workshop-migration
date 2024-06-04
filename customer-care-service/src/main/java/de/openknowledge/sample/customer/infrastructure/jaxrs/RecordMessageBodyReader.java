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
package de.openknowledge.sample.customer.infrastructure.jaxrs;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import de.openknowledge.sample.customer.infrastructure.common.AbstractRecordConverter;

@Provider
@ApplicationScoped
@Consumes({ APPLICATION_JSON, APPLICATION_FORM_URLENCODED })
public class RecordMessageBodyReader extends AbstractRecordConverter implements MessageBodyReader<Record> {

    @Context
    private Providers providers;
    @Context
    private HttpServletRequest request;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isRecord();
    }

    @Override
    public Record readFrom(Class<Record> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
    	Map<String, Object> value;
    	if (APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
    		MessageBodyReader<Map> jsonReader = providers.getMessageBodyReader(Map.class, Map.class, annotations, mediaType);
    		value = jsonReader.readFrom(Map.class, Map.class, annotations, mediaType, httpHeaders, entityStream);
    	} else {
    		value = request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue()[0]));
    	}
        return type.cast(instantiate(type, value));
    }
}
