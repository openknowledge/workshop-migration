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
package de.openknowledge.sample.checkout.infrastructure.jsonb;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

import javax.json.bind.adapter.JsonbAdapter;

import de.openknowledge.sample.checkout.infrastructure.common.ObjectBuilder;

/**
 * A base class to convert records from and to json.
 */
public class AbstractRecordValueTypeAdapter<R extends Record, P> implements JsonbAdapter<R, P> {

    private ObjectBuilder<R> recordBuilder;
    private Method accessor;

    protected AbstractRecordValueTypeAdapter() {
        recordBuilder = ObjectBuilder.<R>fromGenericType(getClass(), AbstractRecordValueTypeAdapter.class, 0);
        RecordComponent[] recordComponents = recordBuilder.getType().getRecordComponents();
        if (recordComponents.length > 1) {
            throw new IllegalStateException(
                "Unsupported record type " + recordBuilder.getType() + ". Only records with one component are supported");
        }
        RecordComponent component = recordComponents[0];
        accessor = component.getAccessor();
    }

    public P adaptToJson(R record) throws Exception {
        return (P)accessor.invoke(record);
    }

    public R adaptFromJson(P jsonValue) throws Exception {
        return recordBuilder.withParameter(jsonValue).build();
    }
}
