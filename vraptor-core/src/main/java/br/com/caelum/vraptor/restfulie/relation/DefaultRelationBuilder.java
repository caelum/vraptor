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
package br.com.caelum.vraptor.restfulie.relation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
 * Default impl for {@link RelationBuilder}
 *
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 * @since 3.2.0
 *
 */
public class DefaultRelationBuilder implements RelationBuilder {

	private final List<Relation> relations = new ArrayList<Relation>();
	private final Proxifier proxifier;
	private final Router router;

	public DefaultRelationBuilder(Router router, Proxifier proxifier) {
		this.router = router;
		this.proxifier = proxifier;
	}

	public void add(Relation relation) {
		relations.add(relation);
	}

	public WithName relation(String name) {
		return new WithNameImpl(name);
	}

	public List<Relation> getRelations() {
		return new ArrayList<Relation>(relations);
	}

	public <T> T relation(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				T instance = relation(method.getName()).uses(type);
				new Mirror().on(instance).invoke().method(method).withArgs(args);
				return null;
			}
		});
	}

	private class WithNameImpl implements WithName {

		private final String name;

		public WithNameImpl(String name) {
			this.name = name;
		}

		public void at(String uri) {
			relations.add(new UriBasedRelation(name, uri));
		}

		public <T> T uses(final Class<T> controller) {
			return proxifier.proxify(controller, new MethodInvocation<T>() {
				public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
					relations.add(new UriBasedRelation(name, router.urlFor(controller, method, args)));
					return null;
				}
			});
		}

	}

}
