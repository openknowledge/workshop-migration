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

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.NoResultException;

@Provider
@ApplicationScoped
public class NoResultExceptionMapper implements ExceptionMapper<NoResultException> {

    private static final Logger LOG = LogManager.getLogger(NoResultExceptionMapper.class);

    @Override
    public Response toResponse(NoResultException exception) {
        LOG.info("Entity not found", exception);
        return Response.status(NOT_FOUND).build();
    }
}
