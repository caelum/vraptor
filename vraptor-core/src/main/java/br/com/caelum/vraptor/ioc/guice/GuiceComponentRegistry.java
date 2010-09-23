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

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Cacheable;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;

import com.google.inject.Binder;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.util.Types;

/**
 * ComponentRegistry for Guice
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class GuiceComponentRegistry implements ComponentRegistry {

	private static final Logger logger = LoggerFactory.getLogger(GuiceComponentRegistry.class);

	private final Binder binder;

	private final Set<Class<?>> boundClasses = new HashSet<Class<?>>();

	public GuiceComponentRegistry(Binder binder) {
		this.binder = binder;
	}
	public void register(Class requiredType, Class componentType) {
		boundClasses.add(requiredType);
		logger.debug("Binding {} to {}", requiredType, componentType);
		bindToConstructor(requiredType, componentType);
		registerFactory(componentType);
	}

	public void deepRegister(Class componentType) {
		register(componentType, componentType);
		deepRegister(componentType, componentType);
	}

	private void deepRegister(Class required, Class component) {
	    if (required == null || required.equals(Object.class)) {
			return;
		}
	    if (boundClasses.add(required)) {
	    	logger.debug("Binding {} to {}", required, component);
	        binder.bind(required).to(component);
	    } else {
	    	logger.debug("Ignoring binding of {} to {}", required, component);
	    }

	    for (Class<?> c : required.getInterfaces()) {
	        deepRegister(c, component);
	    }

	    deepRegister(required.getSuperclass(), component);
	}

	public void registerInScope(Map<Class, Class> classes, Scope scope) {
		for (Entry<Class, Class> entry : classes.entrySet()) {
			bindToConstructor(entry.getKey(), entry.getValue()).in(scope);
			registerFactory(entry.getValue());
		}
	}
	private ScopedBindingBuilder bindToConstructor(Class requiredType, Class componentType) {
		if (componentType.isAnnotationPresent(Cacheable.class)) {
			return binder.bind(requiredType).annotatedWith(Cacheable.class).toConstructor(componentType.getDeclaredConstructors()[0]);
		}
		return binder.bind(requiredType).toConstructor(componentType.getDeclaredConstructors()[0]);
	}

	private void registerFactory(Class componentType) {
		if (ComponentFactory.class.isAssignableFrom(componentType)) {
			final Class<?> target = new ComponentFactoryIntrospector().targetTypeForComponentFactory(componentType);
			Type adapterType = Types.newParameterizedType(ComponentFactoryProviderAdapter.class, target);
			Type factoryType = Types.newParameterizedType(ComponentFactory.class, target);
//			binder.bind(TypeLiteral.get(adapterType));
			binder.bind(TypeLiteral.get(factoryType)).to(componentType);
			binder.bind(target).toProvider((TypeLiteral) TypeLiteral.get(adapterType));
		}
	}

}