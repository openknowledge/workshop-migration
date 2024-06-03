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

import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;
import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

/**
 * A class to build objects from generic type parameters.
 */
public class ObjectBuilder<O> {

    private final Class<O> type;
    private final List<Object> parameters = new ArrayList<>();

    ObjectBuilder(Class<O> objectType) {
        type = notNull(objectType);
    }

    ObjectBuilder(Class<O> objectType, Object parameter) {
        type = notNull(objectType);
        parameters.add(parameter);
    }

    public static <V> ObjectBuilder<V> fromGenericType(Class<?> subclass, Class<?> superclass) {
        return fromGenericType(subclass, superclass, 0);
    }

    public static <V> ObjectBuilder<V> fromGenericType(Class<?> subclass, Class<?> superclass, int parameterIndex) {
        Class<?> directSubclass = subclass;
        while (directSubclass.getSuperclass() != superclass) {
            directSubclass = directSubclass.getSuperclass();
        }

        Type genericSuperclass = directSubclass.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            throw new IllegalStateException(
                "Generic type argument missing for superclass " + superclass.getSimpleName());
        }

        ParameterizedType parameterizedSuperclass = (ParameterizedType)genericSuperclass;
        Type valueType = parameterizedSuperclass.getActualTypeArguments()[parameterIndex];
        if (valueType instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable<?>)valueType;
            TypeVariable<?>[] typeParameters = directSubclass.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                if (typeParameters[i].getName().equals(variable.getName())) {
                    return fromGenericType(subclass, directSubclass, i);
                }
            }
            throw new IllegalStateException(variable + " cannot be resolved");
        }
        return new ObjectBuilder<V>((Class<V>)valueType);
    }

    public static <V> ObjectBuilder<V> forType(Class<V> type) {
        return new ObjectBuilder<V>(type);
    }

    public Class<O> getType() {
        return type;
    }

    public ObjectBuilder<O> withParameter(Object parameter) {
        // returning new instance to support reusability of the builder
        return new ObjectBuilder<O>(type, parameter);
    }

    public ObjectBuilder<O> andParameter(Object parameter) {
        parameters.add(parameter);
        return this;
    }

    public O build() {
        try {
            Object[] arguments = parameters.toArray();
            Constructor<O> constructor = resolveConstructor(arguments);
            if (constructor == null) {
                constructor = resolveConstructorWithParameterConversion(arguments);
                if (constructor == null) {
                    throw new IllegalStateException(
                        arguments.length == 0 ? "No default constructor found in class " + type.getName()
                        : "No suitable constructor found for parameters " + Arrays.toString(arguments)
                        + " in class " + type.getName());
                }

                arguments = convertArguments(constructor, arguments);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            } else {
                throw new IllegalStateException(e.getTargetException());
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private Constructor<O> resolveConstructor(Object[] arguments) {
        return resolveConstructor(arguments, false);
    }

    private Constructor<O> resolveConstructorWithParameterConversion(Object[] arguments) {
        return resolveConstructor(arguments, true);
    }

    private Constructor<O> resolveConstructor(Object[] arguments, boolean convertParameters) {
        Class<?>[] parameterTypes = new Class[arguments.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Object parameter = arguments[i];
            parameterTypes[i] = parameter != null ? parameter.getClass() : null;
        }
        return resolveConstructor(parameterTypes, convertParameters);
    }

    private Constructor<O> resolveConstructor(Class<?>[] parameterTypes, boolean convertParameters) {
        Constructor<O> resolvedConstructor = null;
        Constructor<O> ambiguousConstructor = null;
        for (Constructor<O> constructor : (Constructor<O>[])type.getDeclaredConstructors()) {
            if (matches(constructor, parameterTypes, convertParameters)) {
                if (resolvedConstructor == null || isMoreSpecific(constructor, resolvedConstructor)) {
                    resolvedConstructor = constructor;
                    ambiguousConstructor = null;
                } else if (!isMoreSpecific(resolvedConstructor, constructor)) {
                    ambiguousConstructor = constructor;
                }
            }
        }

        if (ambiguousConstructor != null) {
            throw new IllegalStateException(
                "More that one constructor found for parameter types " + Arrays.asList(parameterTypes) + ". Found "
                + ambiguousConstructor + " and " + resolvedConstructor);
        }

        return resolvedConstructor;
    }

    private boolean matches(Constructor<O> constructor, Class<?>[] parameterTypes, boolean convertParameterTypes) {
        Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
        if (constructorParameterTypes.length != parameterTypes.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (constructorParameterTypes[i].isPrimitive()) {
                constructorParameterTypes[i] = primitiveToWrapper(constructorParameterTypes[i]);
            }

            if (parameterTypes[i] != null && !constructorParameterTypes[i].isAssignableFrom(parameterTypes[i])) {
                if (!convertParameterTypes || !isConvertible(parameterTypes[i], constructorParameterTypes[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isMoreSpecific(Constructor<O> constructor, Constructor<O> resolvedConstructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Class<?>[] resolvedParameterTypes = resolvedConstructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!resolvedParameterTypes[i].isAssignableFrom(parameterTypes[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isConvertible(Class<?> sourceType, Class<?> targetType) {
        try {
            targetType.getDeclaredConstructor(sourceType);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Object[] convertArguments(Constructor<O> constructor, Object[] arguments)
            throws ReflectiveOperationException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < arguments.length; i++) {
            if (parameterTypes[i].isPrimitive()) {
                parameterTypes[i] = primitiveToWrapper(parameterTypes[i]);
            }

            if (!parameterTypes[i].isInstance(arguments[i])) {
                Constructor<?> convertingConstructor = parameterTypes[i].getDeclaredConstructor(arguments[i].getClass());
                convertingConstructor.setAccessible(true);
                arguments[i] = convertingConstructor.newInstance(arguments[i]);
            }
        }

        return arguments;
    }

    private <T> T convertArgument(Class<T> argumentType, Object argument) throws ReflectiveOperationException {
        if (argumentType.isInstance(argument)) {
            return argumentType.cast(argument);
        }

        Constructor<T> convertingConstructor = resolveConvertingConstructor(argumentType, argument);
        convertingConstructor.setAccessible(true);

        return convertingConstructor.newInstance(argument);
    }

    private <T> Constructor<T> resolveConvertingConstructor(Class<T> resultType, Object argument)
            throws NoSuchMethodException {
        for (Constructor convertingConstructor : resultType.getDeclaredConstructors()) {
            Class[] parameterTypes = convertingConstructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0] == argument.getClass()) {
                return convertingConstructor;
            }
        }

        if (ClassUtils.isPrimitiveWrapper(argument.getClass())) {
            Class<?> primitiveType = ClassUtils.wrapperToPrimitive(argument.getClass());
            for (Constructor convertingConstructor : resultType.getDeclaredConstructors()) {
                Class[] parameterTypes = convertingConstructor.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0] == primitiveType) {
                    return convertingConstructor;
                }
            }
        }

        throw new IllegalStateException(
                "No constructor found in " + resultType.getSimpleName() + " for " + argument.getClass());
    }
}
