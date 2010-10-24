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

import br.com.caelum.vraptor.View;

/**
 * A common forward/redirect/include page result.
 *
 * @author Guilherme Silveira
 */
public interface PageResult extends View {

	/**
	 * Server side forwarding to a result.
	 */
	void defaultView();
	
	/**
	 * Server side forwarding to a specific url.
	 */
	void forwardTo(String url);
	
	/**
	 * Server side include a result.
	 */
	void include();

	/**
	 * Client side redirect to an specific url.
	 *
	 * @param url
	 */
	void redirectTo(String url);
	
	/**
	 * Render the default view of given logic, without executing the logic.
	 * Ex: result.use(page()).of(ClientsController.class).list();
	 * will render the view /WEB-INF/jsp/clients/list.jsp without calling list method.
	 */
	<T> T of(Class<T> controllerType);
	
	/**
	 * @deprecated  As of 3.2, replaced by
	 *              {@link #redirectTo(String url)}
	 */
	void redirect(String url);
	
	/**
	 * @deprecated  As of 3.2, replaced by
	 *              {@link #forwardTo(String url)}
	 */
	void forward(String url);
	
	/**
	 * @deprecated  As of 3.2, replaced by
	 *              {@link #forwardTo()}
	 */
	void forward();

}