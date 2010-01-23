/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
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

package br.com.caelum.vraptor.restfulie;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.Routes;

/**
 * A transition which will invoke a controller's method.
 * 
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 */
public class ControllerTransition implements Relation {

	private final Class<?> controller;
	private final String name;
	private final Routes routes;
	private final Method method;
	private final Object[] parameters;

	public ControllerTransition(Class<?> controller, String rel, Method method,
			Object[] parameters, Routes routes) {
		this.controller = controller;
		this.name = rel;
		this.method = method;
		this.parameters = parameters;
		this.routes = routes;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		Object object = routes.uriFor(controller);
		method.setAccessible(true);
		try {
			method.invoke(object, parameters);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to retrieve uri for "
					+ name + " from " + controller.getName(), e);
		}
		return routes.getUri();
	}

	public boolean matches(Method method) {
		return this.method.equals(method);
	}

}
