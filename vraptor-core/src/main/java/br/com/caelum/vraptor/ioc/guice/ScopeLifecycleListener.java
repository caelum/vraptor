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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 *
 * Listens for guice bindings
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
final class ScopeLifecycleListener implements TypeListener {

	private static final Logger logger = LoggerFactory.getLogger(ScopeLifecycleListener.class);
	private final LifecycleScope scope;

	public ScopeLifecycleListener(LifecycleScope scope) {
		this.scope = scope;
	}

	public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
		final List<Method> constructs = new ArrayList<Method>();
		final List<Method> destroys = new ArrayList<Method>();
		extractLifecycleMethods(literal, constructs, destroys);

		logger.debug("Registering lifecycle listeners for {}", literal);

		if (!constructs.isEmpty() || !destroys.isEmpty()) {
			encounter.register(new LifecycleExecutor(constructs, destroys));
		}
	}

	private <I> void extractLifecycleMethods(TypeLiteral<I> literal, final List<Method> constructs,
			final List<Method> destroys) {
		for (Method method : new Mirror().on(literal.getRawType()).reflectAll().methods()) {
			if (method.isAnnotationPresent(PostConstruct.class)) {
				constructs.add(method);
			}

			if (method.isAnnotationPresent(PreDestroy.class)) {
				destroys.add(method);
			}
		}
	}

	private final class LifecycleExecutor implements InjectionListener {
		private final List<Method> destroys;
		private final List<Method> constructs;

		private LifecycleExecutor(List<Method> constructs, List<Method> destroys) {
			this.destroys = destroys;
			this.constructs = constructs;
		}

		public void afterInjection(final Object instance) {
			for (Method method : constructs) {
				new Mirror().on(instance).invoke().method(method).withoutArgs();
			}
			scope.registerDestroyListener(new LifecycleListener() {
				public void onEvent() {
					for (Method method : destroys) {
						new Mirror().on(instance).invoke().method(method).withoutArgs();
					}
				}
			});
		}
	}

}