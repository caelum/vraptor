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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Cacheable;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
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
	private final Set<Class<?>> listTypes = new HashSet<Class<?>>();

	private final Multibinder<StereotypeHandler> stereotypeHandlers;

	public GuiceComponentRegistry(Binder binder, Multibinder<StereotypeHandler> stereotypeHandlers) {
		this.binder = binder;
		this.stereotypeHandlers = stereotypeHandlers;
	}
	public void register(Class requiredType, Class componentType) {
		boundClasses.add(requiredType);
		logger.debug("Binding {} to {}", requiredType, componentType);
		if (StereotypeHandler.class.isAssignableFrom(requiredType)) {
			stereotypeHandlers.addBinding().to(requiredType);
		}
		ScopedBindingBuilder binding = bindToConstructor(requiredType, componentType);
		if (defaultScope(componentType)) {
			binding.in(GuiceProvider.REQUEST);
		}
		registerFactory(componentType);
	}

	private boolean defaultScope(Class componentType) {
		for(Annotation annotation : componentType.getAnnotations()) {
			if (annotation.annotationType().isAnnotationPresent(ScopeAnnotation.class)) {
				return false;
			}
		}
		return true;
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
		Constructor constructor = getConstructor(componentType);
		for (Type type : constructor.getGenericParameterTypes()) {
			if (type instanceof ParameterizedType) {
				ParameterizedType ptype =((ParameterizedType) type);
				if (ptype.getRawType() instanceof Class<?> && List.class.isAssignableFrom((Class<?>) ptype.getRawType())
						&& ptype.getRawType() instanceof Class<?> && !listTypes.contains(ptype.getActualTypeArguments()[0])) {
					listTypes.add((Class<?>) ptype.getActualTypeArguments()[0]);
					registerListType((Class<?>) ptype.getActualTypeArguments()[0], binder);
				}
			}
		}
		return binder.bind(requiredType).toConstructor(constructor);
	}
	private Constructor getConstructor(Class componentType) {
		Constructor[] constructors = componentType.getDeclaredConstructors();
		Iterable<Constructor> filteredConstructor = Iterables.filter(Lists.newArrayList(constructors), new Predicate<Constructor>() {

			public boolean apply(Constructor constructor) {
				return constructor.isAnnotationPresent(Inject.class);
			}
			
		});
		return Iterables.getFirst(filteredConstructor, constructors[0]); 
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
	private <T> void registerListType(Class<T> type, Binder binder) {
		final AllImplementationsProvider<T> provider = new AllImplementationsProvider<T>();
		binder.bindListener(VRaptorAbstractModule.type(Matchers.subclassesOf(type)), new TypeListener() {
			public void hear(TypeLiteral literal, TypeEncounter encounter) {
				provider.addType(literal.getRawType());
			}
		});
		binder.bind(TypeLiteral.get(Types.listOf(type))).toProvider((Provider)provider);
		binder.requestInjection(provider);
	}

}