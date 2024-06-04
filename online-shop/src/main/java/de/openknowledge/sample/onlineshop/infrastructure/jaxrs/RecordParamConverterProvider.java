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

import static de.openknowledge.sample.onlineshop.infrastructure.common.ObjectBuilder.forType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class RecordParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> recordClass, Type recordTypes, Annotation[] annotations) {
        if (!recordClass.isRecord()) {
            return null;
        }
        return (ParamConverter<T>)createConverter((Class<? extends Record>)recordClass);
    }

    private <R extends Record> ParamConverter<R> createConverter(Class<R> recordClass) {
        return new Converter<R>(recordClass);
    }
    
    private static class Converter<T extends Record> implements ParamConverter<T> {

        private Class<T> recordClass;
        
        public Converter(Class<T> recordClass) {
            this.recordClass = recordClass;
        }

        @Override
        public T fromString(String value) {
            return forType(recordClass).withParameter(value).build();
        }

        @Override
        public String toString(T value) {
            try {
                return recordClass.getRecordComponents()[0].getAccessor().invoke(value).toString();
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
