/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.view;

import java.util.EnumSet;
import java.util.List;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Allows status + header related results.
 *
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Status extends View {
	/**
	 * Returns a Ok status (200)
	 */
	void ok();

	/**
	 * Returns a Created status (201)
	 */
	void created();

	/**
	 * Sets the status to 201 and sets the location to the server's location +
	 * the location content.<br>
	 * created("/order/2") ==> http://localhost:8080/my_context/order/2
	 *
	 * @param location
	 */
	void created(String location);

	/**
	 * Returns a No Content status (204)
	 */
	void noContent();

	/**
	 * Returns a Bad Request (400) status
	 *
	 * Notifies the client that he should not try to execute the same request
	 * again: part of its representation is somehow invalid.
	 *
	 * @param message
	 */
	void badRequest(String message);

	/**
	 * Returns a Bad Request (400) status
	 *
	 * Notifies the client that he should not try to execute the same request
	 * again: part of its representation is somehow invalid.
	 *
	 * Serializes the given List as "errors", with
	 * <pre>
	 *    result.use(representation()).from(errors, "errors").serialize();
	 * </pre>
	 * @param message
	 */
	void badRequest(List<?> errors);

	/**
	 * Returns a Forbidden (403) status. You must specify a reason.
	 *
	 * @param message
	 */
	void forbidden(String message);

	/**
	 * Returns a Not Found (404) status
	 */
	void notFound();

	/**
	 * Returns a Conflict (409) status
	 */
	void conflict();

	void header(String key, String value);


	void methodNotAllowed(EnumSet<HttpMethod> allowedMethods);

	/**
	 * Returns a Not Modified (304) status
	 */
	void notModified();

	/**
	 * Send redirect with Moved Permanently (301) Header Example:
	 * result.use(http()).movedPermanentlyTo("/clients"); will move to
	 * /<contextPath>/clients
	 *
	 * @param uri
	 *            absolute uri to redirect
	 */
	void movedPermanentlyTo(String url);

	/**
	 * same as movedPermanentlyTo(String), but will use the url for
	 * controller.method(args);
	 *
	 * Example:
	 * result.use(http()).movedPermanentlyTo(ClientsController.class).list();
	 */
	<T> T movedPermanentlyTo(Class<T> controller);

	/**
	 * The media type sent to the server is not supported.(415)
	 *
	 * @param errorMessage
	 */
	void unsupportedMediaType(String errorMessage);

	/**
	 * The accepted media type is not supported.(406)
	 *
	 * @param errorMessage
	 */
	void notAcceptable();

	
	/**
	 * Return Accepted (202) Status 
	 */
	void accepted();

}
