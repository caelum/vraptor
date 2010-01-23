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

package br.com.caelum.vraptor.rest;

import java.util.List;

/**
 * Basic restfulie implementation: use this interface to implement and let the
 * serializer know which are the possible followign transitions from this
 * object's state.
 * 
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 * 
 */
public interface HypermediaResource {
	public List<Transition> getRelations(Restfulie control);
}
