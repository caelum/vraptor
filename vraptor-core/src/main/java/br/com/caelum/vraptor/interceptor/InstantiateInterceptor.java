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

package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Using a request scoped container, instantiates a resource.<br/>
 * Only instantiates the resource if it has not been instantiated so far, thus
 * allowing composition of another component instantiator and this one.
 *
 * @author Guilherme Silveira
 */
@Intercepts(after=ResourceLookupInterceptor.class)
public class InstantiateInterceptor implements Interceptor {

	private final Container container;

	public InstantiateInterceptor(Container container) {
		this.container = container;
	}

	public void intercept(InterceptorStack invocation, ResourceMethod method,
			Object instance) throws InterceptionException {
		if (instance == null) {
			Class<?> type = method.getResource().getType();
			instance = container.instanceFor(type);
		}
		invocation.next(method, instance);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

}
