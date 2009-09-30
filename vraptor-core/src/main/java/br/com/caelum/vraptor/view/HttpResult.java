/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;

/**
 * A view that deals with HTTP protocol, sending status and error codes
 * and adding headers.
 *
 * All methods of this interface have the same meaning as the corresponding
 * methods on {@link HttpServletResponse}
 * @author Lucas Cavalcanti
 *
 */
public interface HttpResult extends View {

	void sendError(int statusCode);

	HttpResult setStatusCode(int statusCode);

	HttpResult addHeader(String name, String value);

	HttpResult addIntHeader(String name, int value);

	HttpResult addDateHeader(String name, long date);


	/**
	 * Send redirect with Moved Permanently Header
	 * Example:
	 * result.use(http()).movedPermanentlyTo("/clients");
	 * will move to /<contextPath>/clients
	 *
	 * @param uri absolute uri to redirect
	 */
	void movedPermanentlyTo(String url);

	/**
	 * same as movedPermanentlyTo(String), but will use
	 * the url for controller.method(args);
	 *
	 * Example:
	 * result.use(http()).movedPermanentlyTo(ClientsController.class).list();
	 */
	<T> T movedPermanentlyTo(Class<T> controller);
}
