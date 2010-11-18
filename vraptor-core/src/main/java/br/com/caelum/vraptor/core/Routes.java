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

package br.com.caelum.vraptor.core;

import java.util.EnumSet;

import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Allows easy access to detect any route information.<br>
 * In order to access the uri for a method, one should invoke
 * <pre>routes.uriFor(OrderController.class).get(order); String uri = routes.getUri();</pre>
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Routes {

	/**
	 * Analyzes an uri for a specific controller.
	 * @param <T>	the controller
	 * @param type	the controller type
	 * @return
	 */
	public <T> T uriFor(final Class<T> type);

	/**
	 * Returns the last analyzed uri.
	 */
	public String getUri();

	/**
	 * Returns an enumset of allowed methods for this specific uri. 
	 */
	EnumSet<HttpMethod> allowedMethodsFor(String uri);

}
