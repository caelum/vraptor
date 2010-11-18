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

import java.util.List;


/**
 * Controls the process of creating new relations.
 *
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 * @since 3.2.0
 */
public interface RelationBuilder {

	/**
	 * Creates a named relation
	 */
	WithName relation(String name);

	interface WithName {
		/**
		 * Uses given controller method as uri
		 */
		<T> T uses(Class<T> controller);

		/**
		 * uses given uri for the relation
		 */
		void at(String uri);
	}

	/**
	 * adds a relation
	 * @param relation
	 */
	void add(Relation relation);

	/**
	 * Creates a relation using default name
	 */
	<T> T relation(Class<T> type);

	List<Relation> getRelations();

}
