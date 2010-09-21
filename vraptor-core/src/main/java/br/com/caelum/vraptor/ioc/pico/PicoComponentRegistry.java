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

package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
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
public class PicoComponentRegistry extends AbstractComponentRegistry {

    public static final String CONTAINER_SESSION_KEY = PicoComponentRegistry.class.getName() + ".session";

    private static final Logger logger = LoggerFactory.getLogger(PicoComponentRegistry.class);

    private final Map<Class<?>, Class<?>> applicationScoped = new HashMap<Class<?>, Class<?>>();
    private final Map<Class<?>, Class<?>> sessionScoped = new HashMap<Class<?>, Class<?>>();
    private final Map<Class<?>, Class<?>> requestScoped = new HashMap<Class<?>, Class<?>>();
    private final Map<Class<?>, Class<?>> prototypeScoped = new HashMap<Class<?>, Class<?>>();
    private MutablePicoContainer appContainer;
    private boolean initialized = false;

    private final ComponentFactoryRegistry componentFactoryRegistry;

    public PicoComponentRegistry(MutablePicoContainer container, ComponentFactoryRegistry componentFactoryRegistry) {
        this.appContainer = container;
        this.componentFactoryRegistry = componentFactoryRegistry;
    }

    MutablePicoContainer makeChildContainer() {
    	this.appContainer = appContainer.makeChildContainer();
    	return appContainer;
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
            logger.debug("Registering " + type.getName() + " as a session component");
            this.sessionScoped.put(requiredType, type);
        } else if (type.isAnnotationPresent(PrototypeScoped.class)) {
        	logger.debug("Registering " + type.getName() + " as a prototype component");
        	this.prototypeScoped.put(requiredType, type);
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

    /**
     * Registers all application scoped elements into the container.
     */
    public void init() {
        logger.info("Initializing VRaptor IoC Container implementation based on PicoContainer");
        
        for (Map.Entry<Class<?>, Class<?>> entry : applicationScoped.entrySet()) {
            logger.debug("Initializing application scope with key: " + entry.getKey() + ", for component: " +
                    entry.getValue());
            this.appContainer.addComponent(entry.getKey(), entry.getValue());
        }

        registerComponentFactories(appContainer, componentFactoryRegistry.getApplicationMap());

        logger.debug("Session components to initialize: " + sessionScoped.keySet());
        logger.debug("Requets components to initialize: " + requestScoped.keySet());
        this.initialized = true;
    }

    PicoBasedContainer provideRequestContainer(RequestInfo request) {
        MutablePicoContainer parentContainer;
        
        if (sessionScoped.isEmpty()) {
        	logger.debug("There's no @SessionScoped component, so skipping session container creation");
        	parentContainer = this.appContainer;
        } else {
        	parentContainer = getSessionContainer(request);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request components are " + requestScoped);
        }

        MutablePicoContainer requestContainer = new DefaultPicoContainer(new Caching(), 
        			new JavaEE5LifecycleStrategy(new NullComponentMonitor()), parentContainer);
        requestContainer.addComponent(HttpSession.class, request.getRequest().getSession());

        for (Map.Entry<Class<?>, Class<?>> entry : requestScoped.entrySet()) {
            requestContainer.addComponent(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Class<?>, Class<?>> entry : prototypeScoped.entrySet()) {
        	requestContainer.as(Characteristics.NO_CACHE).addComponent(entry.getKey(), entry.getValue());
        }
        requestContainer.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());

        registerComponentFactories(requestContainer, componentFactoryRegistry.getRequestMap());

        return new PicoBasedContainer(requestContainer);
    }

	private MutablePicoContainer getSessionContainer(RequestInfo request) {
		HttpSession session = request.getRequest().getSession();
        MutablePicoContainer sessionScope = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
        if (sessionScope == null) {
            sessionScope = createSessionContainer(session);
        }
		return sessionScope;
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

    private MutablePicoContainer createSessionContainer(HttpSession session) {
        MutablePicoContainer sessionContainer = new DefaultPicoContainer(new Caching(), new JavaEE5LifecycleStrategy(
                new NullComponentMonitor()), this.appContainer);

        sessionContainer.addComponent(HttpSession.class, session);
        session.setAttribute(CONTAINER_SESSION_KEY, sessionContainer);

        if (logger.isDebugEnabled()) {
            logger.debug("Session components are " + sessionScoped);
        }

        for (Map.Entry<Class<?>, Class<?>> entry : sessionScoped.entrySet()) {
            sessionContainer.addComponent(entry.getKey(), entry.getValue());
        }

        registerComponentFactories(sessionContainer, componentFactoryRegistry.getSessionMap());

        sessionContainer.start();
        return sessionContainer;
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
    
    public Collection<Class<?>> getAllRegisteredApplicationScopedComponents() {
    	Collection<Class<?>> components = new HashSet<Class<?>>();
    	components.addAll(applicationScoped.values());
    	components.addAll(sessionScoped.values());
    	components.addAll(requestScoped.values());
    	components.addAll(prototypeScoped.values());
		return components;
	}

}
