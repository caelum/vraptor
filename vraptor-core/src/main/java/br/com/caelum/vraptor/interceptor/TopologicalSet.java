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

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import br.com.caelum.vraptor.Intercepts;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * A set that orders interceptors topologically based on before and after from \@Intercepts
 *
 * @author Lucas Cavalcanti
 * @author David Paniz
 * @author Jose Donizetti
 * @since 3.2.1
 *
 */
public class TopologicalSet {

	static interface FIRST extends Interceptor {};
	private static interface LAST extends Interceptor {};

	private Multimap<Class<? extends Interceptor>, Class<? extends Interceptor>> graph = HashMultimap.create();

	public boolean add(Class<? extends Interceptor> e) {
		Intercepts intercepts = e.getAnnotation(Intercepts.class);
		if (intercepts != null) {
			for (Class<? extends Interceptor> t : intercepts.before()) {
				graph.put(e, t);
			}

			for (Class<? extends Interceptor> t : intercepts.after()) {
				graph.put(t, e);
			}
			if (intercepts.before().length == 0 && intercepts.after().length == 0) {
				graph.put(e, ExecuteMethodInterceptor.class);
				graph.put(ResourceLookupInterceptor.class, e);
			}
		}
		graph.put(FIRST.class, e);
		graph.put(e, LAST.class);

		return true;

	}

	public List<Class<? extends Interceptor>> toList() {
		graph.removeAll(FIRST.class);

		List<Class<? extends Interceptor>> list = Lists.newArrayList();
		iteration(graph.keySet(), list);

		return list;
	}

	private void iteration(Set<Class<? extends Interceptor>> keys, List<Class<? extends Interceptor>> list) {
		if (keys.isEmpty()) {
			return;
		}

		Set<Class<? extends Interceptor>> roots = difference(keys, newHashSet(graph.values())).immutableCopy();

		Preconditions.checkState(!roots.isEmpty(), "Cycle!");

		for (Class<? extends Interceptor> root : roots) {
			list.add(root);
			graph.removeAll(root);
		}
		iteration(graph.keySet(), list);
	}

	public void addAll(Class<? extends Interceptor>... interceptors) {
		for (Class<? extends Interceptor> i : interceptors) {
			add(i);
		}
	}

}
