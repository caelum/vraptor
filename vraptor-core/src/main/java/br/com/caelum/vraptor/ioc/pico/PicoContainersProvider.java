/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.ioc.pico;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.resource.ResourceRegistry;

/**
 * Provides containers, controlling all scopes and registering all different
 * components on their respective areas.
 * 
 * @author Guilherme Silveira
 * @author Adriano Almeida
 */
public class PicoContainersProvider implements ComponentRegistry {

	public static final String CONTAINER_SESSION_KEY = PicoContainersProvider.class.getName() + ".session";

	private static final Logger logger = LoggerFactory.getLogger(PicoContainersProvider.class);

	private final Map<Class<?>, Class<?>> applicationScoped = new HashMap<Class<?>, Class<?>>();
	private final Map<Class<?>, Class<?>> sessionScoped = new HashMap<Class<?>, Class<?>>();
	private final Map<Class<?>, Class<?>> requestScoped = new HashMap<Class<?>, Class<?>>();
	private final MutablePicoContainer appContainer;
	private boolean initialized = false;

	public PicoContainersProvider(MutablePicoContainer container) {
		this.appContainer = container;
	}

	@SuppressWarnings("unchecked")
	public void register(Class requiredType, Class type) {
		if (initialized) {
			logger
					.error("VRaptor was already initialized and the contexts were created but you are trying to register a new component. This is nasty. Do not do it: " + requiredType.getName());
		}
		boolean overriding = alreadyRegistered(requiredType);
		if (overriding) {
			logger.debug("Overriding interface " + requiredType.getName() + " with " + type.getName());
		}
		if (type.isAnnotationPresent(ApplicationScoped.class)) {
			logger.debug("Registering " + type.getName() + " as an application component");
			this.applicationScoped.put(requiredType, type);
			if (initialized) {
				if (overriding) {
					logger
							.error("That is really a bad idea. That app scoped component could have been already initialized and used in other components. If so, this is entirely your fault: "
									+ type.getName() + ".");
				}
				this.appContainer.addComponent(type);
			}
		} else if (type.isAnnotationPresent(SessionScoped.class)) {
			logger.debug("Registering " + type.getName() + " a an session component");
			this.sessionScoped.put(requiredType, type);
		} else {
			logger.debug("Registering " + type.getName() + " as a request component");
			this.requestScoped.put(requiredType, type);
		}
	}

	private boolean alreadyRegistered(Class<?> interfaceType) {
		for (Map<Class<?>, Class<?>> scope : new Map[] { applicationScoped, sessionScoped, requestScoped }) {
			if (scope.containsKey(interfaceType)) {
				scope.remove(interfaceType);
				return true;
			}
		}
		return false;
	}

	public Container provide(VRaptorRequest request) {
		HttpSession session = request.getRequest().getSession();
		MutablePicoContainer sessionScope = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
		if (sessionScope == null) {
			sessionScope = createSessionContainer(session);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Request components are " + requestScoped);
		}
		MutablePicoContainer requestContainer = new DefaultPicoContainer(new Caching(), new JavaEE5LifecycleStrategy(
				new NullComponentMonitor()), sessionScope);
		for (Class<?> requiredType : requestScoped.keySet()) {
			requestContainer.addComponent(requestScoped.get(requiredType));
		}
		for (Class<? extends Interceptor> type : this.appContainer.getComponent(InterceptorRegistry.class).all()) {
			requestContainer.addComponent(type);
		}
		requestContainer.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
		// cache(CachedConverters.class, Converters.class);
		PicoBasedContainer baseContainer = new PicoBasedContainer(requestContainer, request, this.appContainer
				.getComponent(ResourceRegistry.class));
		return baseContainer;
	}

	private MutablePicoContainer createSessionContainer(HttpSession session) {
		MutablePicoContainer sessionContainer = new DefaultPicoContainer(new Caching(), new JavaEE5LifecycleStrategy(
				new NullComponentMonitor()), this.appContainer);
		sessionContainer.addComponent(HttpSession.class, session);
		session.setAttribute(CONTAINER_SESSION_KEY, sessionContainer);
		if (logger.isDebugEnabled()) {
			logger.debug("Session components are " + sessionScoped);
		}
		for (Class<?> requiredType : sessionScoped.keySet()) {
			sessionContainer.addComponent(sessionScoped.get(requiredType));
		}
		return sessionContainer;
	}

	/**
	 * Registers all application scoped elements into the container.
	 */
	public void init() {
		for (Class<?> requiredType : applicationScoped.keySet()) {
			Class<?> type = applicationScoped.get(requiredType);
			logger.debug("Initializing application scope with " + type);
			this.appContainer.addComponent(type);
		}
		logger.debug("Session components to initialize: " + sessionScoped.keySet());
		logger.debug("Requets components to initialize: " + requestScoped.keySet());
		this.initialized = true;
	}

}
