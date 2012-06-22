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

import java.util.Set;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;
import br.com.caelum.vraptor.scan.WebAppBootstrap;
import br.com.caelum.vraptor.scan.WebAppBootstrapFactory;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
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

	private boolean stopSession = false;
	static final RequestCustomScope REQUEST = new RequestCustomScope();
	static final SessionCustomScope SESSION = new SessionCustomScope();
	static final ApplicationCustomScope APPLICATION = new ApplicationCustomScope();

	private final class GuiceContainer implements Container {
		public <T> T instanceFor(Class<T> type) {
			return injector.getInstance(type);
		}

		public <T> boolean canProvide(Class<T> type) {
            try {
                return injector.getProvider(type) != null;
            } catch (ConfigurationException e) {
                return false;
            }
		}
	}

	private Injector injector;
	private GuiceContainer container;
	protected ServletContext context;

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
	
	public Container getContainer() {
		return container;
	}

	public void start(ServletContext context) {
		this.context = context;
		APPLICATION.start();
		container = new GuiceContainer();
		injector = Guice.createInjector(Stage.PRODUCTION, Modules.override(new VRaptorAbstractModule(context, container)).with(customModule()));
		executeStereotypeHandlers();
		injector.injectMembers(REQUEST);
		injector.injectMembers(SESSION);
	}

	private void executeStereotypeHandlers() {
		Set<StereotypeHandler> handlers = injector.getInstance(Key.get(new TypeLiteral<Set<StereotypeHandler>>() {}));
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
				ComponentRegistry registry = new GuiceComponentRegistry(binder, Multibinder.newSetBinder(binder, StereotypeHandler.class));
				BasicConfiguration config = new BasicConfiguration(context);

			    // using the new vraptor.scan
			    WebAppBootstrap webAppBootstrap = new WebAppBootstrapFactory().create(config);
			    webAppBootstrap.configure(registry);

			    // call old-style custom components registration
			    registerCustomComponents(registry);
			}
		};
	}

	protected void registerCustomComponents(ComponentRegistry registry) {
		/* TODO: For now, this is an empty hook method to enable subclasses to use
		 * the scanner and register their specific components.
		 *
		 * In the future, if we scan the classpath for StereotypeHandlers, we can
		 * eliminate this hook.
		 */
	}
	
	protected void stopSession(Boolean value) {
		this.stopSession = value;
	}

	public void stop() {
		if(stopSession) {
			SESSION.stopAll();
		}
		APPLICATION.stop();
	}

}
