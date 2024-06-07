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
package de.openknowledge.sample.checkout.infrastructure.common;

import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public abstract class AbstractRecordConverter {
    
    protected Object instantiate(Type type, Object value, Set<ConstraintViolation<?>> violations) {
        if (value instanceof Map) {
            return instantiate((Class<?>)type, ((Map<String, Object>)value)::get, violations);
        } else if (value instanceof List) {
            return instantiate(type, (List<?>)value, violations);
        } else if (value instanceof String) {
            return instantiate((Class<?>)type, (String)value, violations);
        } else if ((value instanceof String[]) && ((String[])value).length == 1) {
            return instantiate((Class<?>)type, ((String[])value)[0], violations);
        } else if (value instanceof Number) {
            return instantiate((Class<?>)type, (Number)value, violations);
        } else if (value instanceof Boolean) {
            return instantiate((Class<?>)type, (Boolean)value, violations);
        } else if (value == null) {
            return null;
        } else {
            throw new IllegalArgumentException("Unexpected value: " + value.getClass());
        }
    }

    protected Object instantiate(Class<?> type, Function<String, Object> value, Set<ConstraintViolation<?>> violations) {
        RecordComponent[] recordComponents = type.getRecordComponents();
        Class<?>[] parameterTypes = new Class[recordComponents.length];
        Object[] parameters = new Object[recordComponents.length];
        for (int i = 0; i < recordComponents.length; i++) {
            Type parameterType = recordComponents[i].getType();
            parameterTypes[i] = recordComponents[i].getType();
            if (List.class.equals(parameterTypes[i])) {
                parameterType = recordComponents[i].getGenericType();
            }
            parameters[i] = instantiate(parameterType, value.apply(recordComponents[i].getName()), violations);
        }
        return instantiate(type, parameterTypes, parameters, violations);
    }

    private Object instantiate(Type type, List<?> value, Set<ConstraintViolation<?>> violations) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Unsupported type " + type);
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        List<Object> list = new ArrayList<>();
        for (Object object: value) {
            list.add(instantiate(parameterizedType.getActualTypeArguments()[0], object, violations));
        }
        return list;
    }

    private Object instantiate(Class<?> type, String value, Set<ConstraintViolation<?>> violations) {
        return instantiate(type, new Class<?>[] {value.getClass()}, new Object[] {value}, violations);
    }

    protected Object instantiate(Class<?> type, Number value, Set<ConstraintViolation<?>> violations) {
        Class<?> targetType = type.getRecordComponents()[0].getType();
        Object convertedValue = instantiate(targetType, new Class[] {String.class}, new Object[] {value.toString()}, violations);
        return instantiate(type, new Class<?>[] {targetType}, new Object[] {convertedValue}, violations);
    }

    protected abstract Validator validator();

    private Object instantiate(Class<?> type, Boolean value, Set<ConstraintViolation<?>> violations) {
        return instantiate(type, new Class<?>[] {value.getClass()}, new Object[] {value}, violations);
    }

    private Object instantiate(Class<?> type, Class<?>[] parameterTypes, Object[] parameters, Set<ConstraintViolation<?>> violations) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            if (violations != null) {
                Set<ConstraintViolation<?>> newViolations = (Set<ConstraintViolation<?>>)(Set<?>)validator()
                    .forExecutables()
                    .validateConstructorParameters(constructor, parameters)
                    .stream()
//                    .map(violation -> new FixedPathConstraintViolation(path, violation))
                    .collect(toSet());
                violations.addAll(newViolations);
                if (!newViolations.isEmpty()) {
                    return null;
                }
            }
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
