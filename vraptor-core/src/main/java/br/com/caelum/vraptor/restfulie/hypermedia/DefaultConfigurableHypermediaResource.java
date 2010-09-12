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
package br.com.caelum.vraptor.restfulie.hypermedia;

import java.util.List;

import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;
/**
 * Default implementation. Uses a {@link RelationBuilder} to record relations and add it later
 * when configuring relation.
 *
 * @author Lucas Cavalcanti
 * @since 3.2.0
 *
 */
public class DefaultConfigurableHypermediaResource implements ConfigurableHypermediaResource {

	private final RelationBuilder delegate;
	private final Object model;

	public DefaultConfigurableHypermediaResource(RelationBuilder delegate, Object model) {
		this.delegate = delegate;
		this.model = model;
	}

	public <T> T getModel() {
		return (T) model;
	}

	public void configureRelations(RelationBuilder builder) {
		for (Relation relation : delegate.getRelations()) {
			builder.add(relation);
		}
	}

	public void add(Relation relation) {
		delegate.add(relation);
	}

	public List<Relation> getRelations() {
		return delegate.getRelations();
	}

	public <T> T relation(Class<T> type) {
		return delegate.relation(type);
	}

	public WithName relation(String name) {
		return delegate.relation(name);
	}

}
