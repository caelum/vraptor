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
package br.com.caelum.vraptor.util.collections;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.base.Function;

public class Functions {

	public static <T> Function<Class<? extends T>, ? extends T> instanceWith(final Container container) {
		return new Function<Class<? extends T>, T>() {
			public T apply(Class<? extends T> type) {
				return container.instanceFor(type);
			}
		};
	}

	public static Function<Route, ResourceMethod> matches(final String uri, final HttpMethod method, final MutableRequest request) {
		return new Function<Route, ResourceMethod>() {
			public ResourceMethod apply(Route route) {
				return route.resourceMethod(request, uri);
			}
		};
	}
}
