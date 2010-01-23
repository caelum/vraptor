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

import java.util.List;

import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

/**
 * Controls the process of creating new relations and notifying the resource
 * representation builder how these relations can be translated into links.
 * 
 * @author lucas cavalcanti
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Restfulie {

	public RelationBuilder transition(String name);

	<T> T transition(Class<T> type);

	public List<Relation> getTransitions();

	/**
	 * TODO Should be removed soon.
	 */
	public void clear();

}
