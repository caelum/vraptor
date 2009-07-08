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
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * Provides containers, controlling all scopes and registering all different
 * components on their respective areas.
 *
 * @author Guilherme Silveira
 * @author Adriano Almeida
 * @author Sérgio Lopes
 */
public class PicoContainersProvider implements ComponentRegistry {

    public static final String CONTAINER_SESSION_KEY = PicoContainersProvider.class.getName() + ".session";

    private static final Logger logger = LoggerFactory.getLogger(PicoContainersProvider.class);

    private final Map<Class<?>, Class<?>> applicationScoped = new HashMap<Class<?>, Class<?>>();
    private final Map<Class<?>, Class<?>> sessionScoped = new HashMap<Class<?>, Class<?>>();
    private final Map<Class<?>, Class<?>> requestScoped = new HashMap<Class<?>, Class<?>>();
    private final MutablePicoContainer appContainer;
    private boolean initialized = false;

    private final ComponentFactoryRegistry componentFactoryRegistry;

    public PicoContainersProvider(MutablePicoContainer container, ComponentFactoryRegistry componentFactoryRegistry) {
        this.appContainer = container;
        this.componentFactoryRegistry = componentFactoryRegistry;
    }

    @SuppressWarnings("unchecked")
    public void register(Class<?> requiredType, Class<?> type) {
        logger.debug("Registering " + requiredType.getName() + " with " + type.getName());

        boolean overriding = alreadyRegistered(requiredType);
        if (overriding) {
            logger.debug("Overriding interface " + requiredType.getName() + " with " + type.getName());
        }
        if (type.isAnnotationPresent(ApplicationScoped.class)) {
            logger.debug("Registering " + type.getName() + " as an application component");
            this.applicationScoped.put(requiredType, type);
            if (initialized) {
                logger.warn("VRaptor was already initialized, the contexts were created but you are registering a component."
                        + "This is nasty. The original component might already be in use."
                        + "Avoid doing it: " + requiredType.getName());
                this.appContainer.addComponent(requiredType, type);
            }
        } else if (type.isAnnotationPresent(SessionScoped.class)) {
            logger.debug("Registering " + type.getName() + " a an session component");
            this.sessionScoped.put(requiredType, type);
        } else {
            // default behaviour: even without @RequestScoped
            if (!type.isAnnotationPresent(RequestScoped.class)) {
                logger.info("Class being registered as @RequestScoped, since there is no Scope annotation " + type);
            }
            logger.debug("Registering " + type.getName() + " as a request component");
            this.requestScoped.put(requiredType, type);
        }

        if (ComponentFactory.class.isAssignableFrom(type) && !requiredType.equals(ComponentFactory.class)) {
            componentFactoryRegistry.register((Class<? extends ComponentFactory<?>>) type);

            if (type.isAnnotationPresent(ApplicationScoped.class) && initialized) {
                if (initialized) {
                    logger.warn("VRaptor was already initialized, the contexts were created but you are registering a component."
                            + "This is nasty. The original component might already be in use."
                            + "Avoid doing it: " + requiredType.getName());
                    Class<?> targetType = new ComponentFactoryIntrospector().targetTypeForComponentFactory(type);
                    this.appContainer.addAdapter(new PicoComponentAdapter(targetType, (Class<? extends ComponentFactory<?>>) type));
                }
            }
        }
    }

    private boolean alreadyRegistered(Class<?> interfaceType) {
        for (Map<Class<?>, Class<?>> scope : new Map[]{applicationScoped, sessionScoped, requestScoped}) {
            if (scope.containsKey(interfaceType)) {
                scope.remove(interfaceType);
                return true;
            }
        }
        return false;
    }

    PicoBasedContainer provide(RequestInfo request) {
        HttpSession session = request.getRequest().getSession();
        MutablePicoContainer sessionScope = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
        if (sessionScope == null) {
            sessionScope = createSessionContainer(session);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Request components are " + requestScoped);
        }
        MutablePicoContainer requestContainer = new VRaptorPicoContainer(sessionScope);
        for (Class<?> requiredType : requestScoped.keySet()) {
            requestContainer.addComponent(requiredType, requestScoped.get(requiredType));
        }
        requestContainer.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());

        registerComponentFactories(requestContainer, componentFactoryRegistry.getRequestScopedComponentFactoryMap());

        return new PicoBasedContainer(requestContainer, this.appContainer.getComponent(Router.class));
    }

    private MutablePicoContainer createSessionContainer(HttpSession session) {
        MutablePicoContainer sessionContainer = new VRaptorPicoContainer(this.appContainer);

        sessionContainer.addComponent(HttpSession.class, session);
        session.setAttribute(CONTAINER_SESSION_KEY, sessionContainer);

        if (logger.isDebugEnabled()) {
            logger.debug("Session components are " + sessionScoped);
        }

        for (Class<?> requiredType : sessionScoped.keySet()) {
            sessionContainer.addComponent(requiredType, sessionScoped.get(requiredType));
        }

        registerComponentFactories(sessionContainer, componentFactoryRegistry.getSessionScopedComponentFactoryMap());

        sessionContainer.start();
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

        registerComponentFactories(appContainer, componentFactoryRegistry.getApplicationScopedComponentFactoryMap());

        logger.debug("Session components to initialize: " + sessionScoped.keySet());
        logger.debug("Requets components to initialize: " + requestScoped.keySet());
        this.initialized = true;
    }

    /**
     * Register all component factories found in classpath scanning
     *
     * @param container
     * @param componentFactoryMap
     */
    private void registerComponentFactories(MutablePicoContainer container, Map<Class<?>, Class<? extends ComponentFactory>> componentFactoryMap) {
        for (Class<?> targetType : componentFactoryMap.keySet()) {
            container.addAdapter(new PicoComponentAdapter(targetType, componentFactoryMap.get(targetType)));
        }
    }

}
