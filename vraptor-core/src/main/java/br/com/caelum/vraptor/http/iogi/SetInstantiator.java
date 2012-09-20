/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

/**
 * An instantiator that supports Sets.
 * 
 * @author Otávio Scherer Garcia
 * @author Lucas Cavalcanti
 * @since 3.5.0-SNAPSHOT
 */
public class SetInstantiator implements Instantiator<Set<Object>> {
	
	private ListInstantiator listInstantiator;

	public SetInstantiator(Instantiator<Object> listElementInstantiator) {
		listInstantiator = new ListInstantiator(listElementInstantiator);
		
	}
	
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isAssignableFrom(Set.class);
	}
	
	public Set<Object> instantiate(Target<?> target, Parameters parameters) {
		List<Object> list = listInstantiator.instantiate(target, parameters);
		
		if (list == null) {
			return null;
		}
		
		return new HashSet<Object>(list);
	}
}
