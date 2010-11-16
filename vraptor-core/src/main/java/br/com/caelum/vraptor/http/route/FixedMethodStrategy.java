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

package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.Stringnifier;

/**
 * A route strategy which invokes a fixed type's method.
 *
 * @author guilherme silveira
 */
public class FixedMethodStrategy implements Route {

	private final ResourceMethod resourceMethod;

	private final EnumSet<HttpMethod> methods;

	private final ParametersControl parameters;

	private final int priority;

	private final String originalUri;

	public FixedMethodStrategy(String originalUri, Class<?> type, Method method, Set<HttpMethod> methods,
			ParametersControl control, int priority) {
		this.originalUri = originalUri;
		this.methods = methods.isEmpty() ? EnumSet.allOf(HttpMethod.class) : EnumSet.copyOf(methods);
		this.parameters = control;
		this.resourceMethod = new DefaultResourceMethod(new DefaultResourceClass(type), method);
		this.priority = priority;
	}

	public boolean canHandle(Class<?> type, Method method) {
		return type.equals(this.resourceMethod.getResource().getType())
				&& method.equals(this.resourceMethod.getMethod());
	}

	public ResourceMethod resourceMethod(MutableRequest request, String uri) {
		parameters.fillIntoRequest(uri, request);
		return this.resourceMethod;
	}

	public EnumSet<HttpMethod> allowedMethods() {
		return methods;
	}

	public boolean canHandle(String uri) {
		return parameters.matches(uri);
	}

	public String urlFor(Class<?> type, Method m, Object params) {
		return parameters.fillUri(params);
	}

	public int getPriority() {
		return this.priority;
	}

	@Override
	public String toString() {
		return "[FixedMethodStrategy: " + originalUri + " " + Stringnifier.simpleNameFor(resourceMethod.getMethod())
				+ " " + methods + "]";
	}
}
