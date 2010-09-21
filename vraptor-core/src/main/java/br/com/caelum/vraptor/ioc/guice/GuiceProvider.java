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

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 *
 * A Container Provider that uses Google Guice as DI container.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class GuiceProvider implements ContainerProvider {

	static final LifecycleScope REQUEST = new RequestCustomScope();
	static final LifecycleScope SESSION = new SessionCustomScope();
	static final LifecycleScope APPLICATION = new ApplicationCustomScope();

	private final class GuiceContainer implements Container {
		public <T> T instanceFor(Class<T> type) {
			return injector.getInstance(type);
		}

		public <T> boolean canProvide(Class<T> type) {
			return instanceFor(type) != null;
		}
	}

	private Injector injector;
	private GuiceContainer container;

	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
		VRaptorRequestHolder.setRequestForCurrentThread(request);
		REQUEST.start();
		try {
			return execution.insideRequest(container);
		} finally {
			REQUEST.stop();
			VRaptorRequestHolder.resetRequestForCurrentThread();
		}
	}

	public void start(ServletContext context) {
		APPLICATION.start();
		container = new GuiceContainer();
		injector = Guice.createInjector(Modules.override(new VRaptorAbstractModule(context, container)).with(customModule()));
		Map<Key<?>, Binding<?>> bindings = Maps.filterKeys(injector.getAllBindings(), new Predicate<Key<?>>() {
			public boolean apply(Key<?> key) {
				return StereotypeHandler.class.isAssignableFrom(key.getTypeLiteral().getRawType());
			}
		});
		Collection<StereotypeHandler> handlers = Collections2.transform(bindings.keySet(), new Function<Key<?>, StereotypeHandler>() {
			public StereotypeHandler apply(Key<?> key) {
				return (StereotypeHandler) injector.getInstance(key);
			}
		});
		for (Key<?> key : injector.getAllBindings().keySet()) {
			for (StereotypeHandler handler : handlers) {
				Class<?> type = key.getTypeLiteral().getRawType();
				if (type.isAnnotationPresent(handler.stereotype())) {
					handler.handle(type);
				}
			}
		}
	}

	protected Module customModule() {
		return new Module() {
			public void configure(Binder binder) {
			}
		};
	}
	
	protected void registerCustomComponents(ComponentRegistry registry) {
		
	}

	public void stop() {
		APPLICATION.stop();
	}

}
