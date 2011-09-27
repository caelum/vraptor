/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Set;

import br.com.caelum.vraptor.resource.HttpMethod;

public interface RouteBuilder {

	ParameterControlBuilder withParameter(String name);
	public interface ParameterControlBuilder {

		RouteBuilder ofType(Class<?> type);

		RouteBuilder matching(String regex);

	}

	<T> T is(final Class<T> type);

	void is(Class<?> type, Method method);

	/**
	 * Accepts also this http method request. If this method is not invoked, any
	 * http method is supported, otherwise all parameters passed are supported.
	 *
	 * @param method
	 * @return
	 */
	RouteBuilder with(HttpMethod method);

	/**
	 * Accepts also all given http methods.
	 *
	 * @param method
	 * @return
	 */
	RouteBuilder with(Set<HttpMethod> methods);

	/**
	 * Changes Route priority
	 *
	 * @param priority
	 * @return
	 */
	RouteBuilder withPriority(int priority);

	Route build();
}