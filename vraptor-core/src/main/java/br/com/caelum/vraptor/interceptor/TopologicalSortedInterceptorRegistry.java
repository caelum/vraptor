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
package br.com.caelum.vraptor.interceptor;

import java.util.List;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * An interceptor registry that sorts interceptors based on their before and after conditions
 *
 * @author Lucas Cavalcanti
 * @author David Paniz
 * @since 3.3.0
 *
 */
@ApplicationScoped
public class TopologicalSortedInterceptorRegistry implements InterceptorRegistry {

	private Graph<Class<? extends Interceptor>> set = new Graph<Class<? extends Interceptor>>();

	public List<Class<? extends Interceptor>> all() {
		return set.topologicalOrder();
	}

	public void register(Class<? extends Interceptor>... interceptors) {
		for (Class<? extends Interceptor> interceptor : interceptors) {
			Intercepts intercepts = interceptor.getAnnotation(Intercepts.class);
			if (intercepts != null) {
				addEdges(interceptor, intercepts.before(), intercepts.after());
			} else {
				addDefaultEdges(interceptor);
			}
		}
	}

	private void addDefaultEdges(Class<? extends Interceptor> interceptor) {
		set.addEdge(interceptor, ExecuteMethodInterceptor.class);
		if (!interceptor.equals(ResourceLookupInterceptor.class)) {
			set.addEdge(ResourceLookupInterceptor.class, interceptor);
		}
	}

	private void addEdges(Class<? extends Interceptor> interceptor, Class<? extends Interceptor>[] before, Class<? extends Interceptor>[] after) {
		set.addEdges(interceptor, before);

		for (Class<? extends Interceptor> other : after) {
			set.addEdge(other, interceptor);
		}
	}

}
