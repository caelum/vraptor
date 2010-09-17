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

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scope;

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

	private static final Scope REQUEST = new RequestCustomScope();
	private static final Scope SESSION = new SessionCustomScope();
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
		try {
			return execution.insideRequest(container);
		} finally {
			VRaptorRequestHolder.resetRequestForCurrentThread();
		}
	}

	public void start(ServletContext context) {
		container = new GuiceContainer();
		injector = Guice.createInjector(new VRaptorAbstractModule(REQUEST, SESSION, context, container));
	}

	public void stop() {
	}

}
