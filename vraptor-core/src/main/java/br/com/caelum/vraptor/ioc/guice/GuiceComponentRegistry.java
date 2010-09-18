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
package br.com.caelum.vraptor.ioc.guice;

import java.util.HashSet;
import java.util.Set;

import br.com.caelum.vraptor.ComponentRegistry;

import com.google.inject.Binder;

/**
 * ComponentRegistry for Guice
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class GuiceComponentRegistry implements ComponentRegistry {
	private final Binder binder;
	public GuiceComponentRegistry(Binder binder) {
		this.binder = binder;
	}
	public void register(Class requiredType, Class componentType) {
		binder.bind(requiredType).toConstructor(componentType.getDeclaredConstructors()[0]);
	}

	public void deepRegister(Class componentType) {
		register(componentType, componentType);
		Set<Class> registered = new HashSet<Class>();
		registered.add(componentType);
		deepRegister(componentType, componentType, registered);
	}

	private void deepRegister(Class required, Class component, Set<Class> registeredKeys) {
	    if (required == null || required.equals(Object.class)) {
			return;
		}

	    if (!registeredKeys.contains(required)) {
	        registeredKeys.add(required);
	        binder.bind(required).to(component);
	    }

	    for (Class<?> c : required.getInterfaces()) {
	        deepRegister(c, component, registeredKeys);
	    }

	    deepRegister(required.getSuperclass(), component, registeredKeys);
	}
}