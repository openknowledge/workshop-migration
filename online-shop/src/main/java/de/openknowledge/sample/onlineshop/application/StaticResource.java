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
package de.openknowledge.sample.onlineshop.application;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.apache.commons.io.IOUtils.copy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/")
public class StaticResource  {

	@GET
	@Path("{filename: [a-z0-9\\-\\_]+\\.[a-z0-9\\\\-]+}")
	public Response getResource(@PathParam("filename") String filename) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("META-INF/resources/%s", filename));
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            StringWriter stringContent = new StringWriter();
            PrintWriter content = new PrintWriter(stringContent)) {
            
            if (filename.endsWith(".png") || filename.endsWith(".jpg")) {
            	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            	copy(inputStream, outputStream);
                return Response.ok(outputStream.toByteArray()).type("image/" + filename.substring(filename.lastIndexOf('.') + 1)).build();
            } else {
                buffer.lines().forEach(content::println);
                String mediaType = filename.endsWith(".js") ? "application/javascript" : "text/" + filename.substring(filename.lastIndexOf('.') + 1);
                return Response.ok(stringContent.toString()).type(mediaType).build();
            }
        } catch (NullPointerException e) {
        	return Response.status(NOT_FOUND).build();
        } catch (IOException e) {
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
	}
}
