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
package de.openknowledge.sample.onlineshop.infrastructure.jsonb;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

import javax.json.bind.adapter.JsonbAdapter;

import de.openknowledge.sample.onlineshop.infrastructure.common.ObjectBuilder;

public class AbstractRecordValueTypeAdapter<R extends Record, P> implements JsonbAdapter<R, P> {

    private ObjectBuilder<R> valueObjectBuilder;
    private Method accessor;

    protected AbstractRecordValueTypeAdapter() {
        valueObjectBuilder = ObjectBuilder.<R>fromGenericType(getClass(), AbstractRecordValueTypeAdapter.class, 0);
        RecordComponent[] recordComponents = valueObjectBuilder.getType().getRecordComponents();
        if (recordComponents.length > 1) {
            throw new IllegalStateException(
                "Unsupported record type " + valueObjectBuilder.getType() + ". Only records with one component are supported");
        }
        RecordComponent component = recordComponents[0];
        accessor = component.getAccessor();
    }

    public P adaptToJson(R record) throws Exception {
        return (P)accessor.invoke(record);
    }

    public R adaptFromJson(P jsonValue) throws Exception {
        return valueObjectBuilder.withValue(jsonValue).build();
    }
}
