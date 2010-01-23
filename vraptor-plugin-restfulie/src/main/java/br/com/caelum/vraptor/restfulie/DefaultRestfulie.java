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
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

/**
 * Helper to create transitions and states when using restfulie.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
@RequestScoped
public class DefaultRestfulie implements Restfulie {
	
	private final List<RelationBuilder> relations = new ArrayList<RelationBuilder>();
	private final Routes routes;
	private final Proxifier proxifier;
	
	public DefaultRestfulie(Routes routes, Proxifier proxifier) {
		this.routes = routes;
		this.proxifier = proxifier;
	}
	
	public String getStatusField() {
		return "status";
	}
	
	public RelationBuilder relation(String name) {
		RelationBuilder builder = createBuilderFor(name);
		this.relations.add(builder);
		return builder;
	}

	/**
	 * Allows the use of different relation builders.
	 */
	protected RelationBuilder createBuilderFor(String name) {
		return new RelationBuilder(name, routes, proxifier);
	}

	public List<Relation> getRelations() {
		List<Relation> transitions = new ArrayList<Relation>();
		for(RelationBuilder builder : this.relations) {
			transitions.add(builder.build());
		}
		return transitions;
	}

	public <T> T relation(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args,
					SuperMethod superMethod) {
				T instance = transition(method.getName()).uses(type);
				try {
					method.invoke(instance, args);
				} catch (Exception e) {
					throw new IllegalArgumentException("Unable to create transition for " + method.getName() + " within " + type.getName(), e);
				}
				return null;
			}
		});
	}

	public void clear() {
		relations.clear();
	}

	public RelationBuilder transition(String name) {
		return relation(name);
	}

	public <T> T transition(Class<T> type) {
		return relation(type);
	}

}
