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
package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.scan.WebAppBootstrap;
import br.com.caelum.vraptor.scan.WebAppBootstrapFactory;

/**
 * @author Fabio Kung
 */
public class SpringProvider implements ContainerProvider {
	private final RequestContextListener requestListener = new RequestContextListener();
	private SpringBasedContainer container;

	/**
	 * Provides request scope support for Spring IoC Container when
	 * org.springframework.web.context.request.RequestContextListener has not
	 * been called.
	 */
	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
		if (springListenerAlreadyCalled()) {
			return execution.insideRequest(getContainer());
		}
		VRaptorRequestHolder.setRequestForCurrentThread(request);
		T result;
		try {
			ServletContext context = request.getServletContext();
			HttpServletRequest webRequest = request.getRequest();
			requestListener.requestInitialized(new ServletRequestEvent(context, webRequest));
			try {
				result = execution.insideRequest(getContainer());
			} finally {
				requestListener.requestDestroyed(new ServletRequestEvent(context, webRequest));
			}
		} finally {
			VRaptorRequestHolder.resetRequestForCurrentThread();
		}
		return result;
	}

	public SpringBasedContainer getContainer() {
		return container;
	}

	public void stop() {
		container.stop();
	}

	/**
	 * You can override this method to start some components, remember to call super before.
	 */
	public void start(ServletContext context) {
		container = new SpringBasedContainer(getParentApplicationContext(context));

		BasicConfiguration config = new BasicConfiguration(context);
		WebAppBootstrap bootstrap = new WebAppBootstrapFactory().create(config);
		bootstrap.configure(container);

		registerCustomComponents(container);
		container.start(context);
	}

	/**
	 * you can override this method for registering custom components, or use
	 * optional vraptor components, like hibernate session and session factory
	 * creators:
	 *
	 * registry.register(SessionCreator.class, SessionCreator.class);
	 * registry.register(SessionFactoryCreator.class,
	 * SessionFactoryCreator.class);
	 *
	 * @param registry
	 */
	protected void registerCustomComponents(ComponentRegistry registry) {

	}

	/**
	 * You can override this method for providing your own Spring
	 * ApplicationContext
	 *
	 * @return your spring application context
	 */
	protected ConfigurableWebApplicationContext getParentApplicationContext(ServletContext context) {
		return new DefaultSpringLocator().getApplicationContext(context);
	}

	private boolean springListenerAlreadyCalled() {
		return RequestContextHolder.getRequestAttributes() != null;
	}

}
