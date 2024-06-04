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
package de.openknowledge.sample.customer.infrastructure.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractRecordConverter {
    
    protected Object instantiate(Type type, Object value) {
        if (value instanceof Map) {
            return instantiate((Class<?>)type, (Map<String, Object>)value);
        } else if (value instanceof List) {
            return instantiate(type, (List<?>)value);
        } else if (value instanceof String) {
            return instantiate((Class<?>)type, (String)value);
        } else if (value instanceof Number) {
            return instantiate((Class<?>)type, (Number)value);
        } else if (value instanceof Boolean) {
            return instantiate((Class<?>)type, (Boolean)value);
        } else if (value == null) {
            return null;
        } else {
            throw new IllegalArgumentException("Unexpected value: " + value.getClass());
        }
    }

    protected Object instantiate(Class<?> type, Map<String, Object> value) {
        RecordComponent[] recordComponents = type.getRecordComponents();
        Class<?>[] parameterTypes = new Class[recordComponents.length];
        Object[] parameters = new Object[recordComponents.length];
        for (int i = 0; i < recordComponents.length; i++) {
            Type parameterType = recordComponents[i].getType();
            parameterTypes[i] = recordComponents[i].getType();
            if (List.class.equals(parameterTypes[i])) {
                parameterType = recordComponents[i].getGenericType();
            }
            parameters[i] = instantiate(parameterType, value.get(recordComponents[i].getName()));
        }
        return instantiate(type, parameterTypes, parameters);
    }

    private Object instantiate(Type type, List<?> value) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Unsupported type " + type);
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        List<Object> list = new ArrayList<>();
        for (Object object: value) {
            list.add(instantiate(parameterizedType.getActualTypeArguments()[0], object));
        }
        return list;
    }

    private Object instantiate(Class<?> type, String value) {
        return instantiate(type, new Class<?>[] {value.getClass()}, value);
    }

    protected Object instantiate(Class<?> type, Number value) {
        Class<?> targetType = type.getRecordComponents()[0].getType();
        Object convertedValue = instantiate(targetType, new Class[] {String.class}, value.toString());
        return instantiate(type, new Class<?>[] {targetType}, convertedValue);
    }

    private Object instantiate(Class<?> type, Boolean value) {
        return instantiate(type, new Class<?>[] {value.getClass()}, value);
    }

    private Object instantiate(Class<?> type, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}

