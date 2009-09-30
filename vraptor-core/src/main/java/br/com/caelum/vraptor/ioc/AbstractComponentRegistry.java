/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
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
package br.com.caelum.vraptor.ioc;

import java.util.HashSet;
import java.util.Set;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * An abstract component registry that provides expected behavior for deepRegistry method.
 * @author Lucas Cavalcanti
 *
 */
public abstract class AbstractComponentRegistry implements ComponentRegistry {

	public final void deepRegister(Class<?> componentType) {
		deepRegister(componentType, componentType, new HashSet<Class<?>>());
	}

	private void deepRegister(Class<?> required, Class<?> component, Set<Class<?>> registeredKeys) {
        if (required == null || required.equals(Object.class)) {
			return;
		}

        if (!registeredKeys.contains(required)) {
            registeredKeys.add(required);
            register(required, component);
        }

        for (Class<?> c : required.getInterfaces()) {
            deepRegister(c, component, registeredKeys);
        }

        deepRegister(required.getSuperclass(), component, registeredKeys);
    }

}
