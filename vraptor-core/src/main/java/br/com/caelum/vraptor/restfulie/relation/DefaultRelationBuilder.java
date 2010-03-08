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

package br.com.caelum.vraptor.restfulie.relation;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;

/**
 * Builder to help creating transitions.
 *
 * @author guilherme silveira
 * @author pedro matiello
 * @since 3.0.3
 */
public class DefaultRelationBuilder implements RelationBuilder {

	private final String name;
	private Class<?> controller;
	private final Routes routes;
	private final Proxifier proxifier;
	private Method method;
	private Object[] parameters = new Object[0];
	private String customUri;

	public DefaultRelationBuilder(String name, Routes routes, Proxifier proxifier) {
		this.name = name;
		this.routes = routes;
		this.proxifier = proxifier;
	}

	@SuppressWarnings("unchecked")
	class Callback implements MethodInvocation{
		public Object intercept(Object proxy, java.lang.reflect.Method method,
				Object[] args, br.com.caelum.vraptor.proxy.SuperMethod superMethod) {
			DefaultRelationBuilder.this.method = method;
			parameters = args;
			return null;
		};
	}

	@SuppressWarnings("unchecked")
	public <T> T uses(Class<T> type) {
		this.controller = type;

		return type.cast(proxifier.proxify(type, new Callback()));
	}

	public RelationBuilder at(String uri) {
		this.customUri = uri;
		return this;
	}

	public Relation build() {
		if(this.customUri != null) {
			return new UriBasedRelation(name, customUri);
		} else if (controller != null) {
			if(method==null) {
				method = findMethod(name, controller);
				parameters = new Object[method.getParameterTypes().length];
			}
			return new ControllerBasedRelation(controller, name, method, parameters, routes);
		}
		throw new IllegalStateException(
				"Relation was not correctly created: '" + name + "'");
	}

	private Method findMethod(String name, Class<?> type) {
		if(type.equals(Object.class)) {
			throw new IllegalArgumentException("Controller " + controller.getName() + " does not have a method named " + name);
		}
		for(Method m : type.getDeclaredMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return findMethod(name, type.getSuperclass());
	}

}
