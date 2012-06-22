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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.scan.WebAppBootstrap;
import br.com.caelum.vraptor.scan.WebAppBootstrapFactory;

/**
 * Managing internal components by using pico container.<br>
 * There is an extension point through the registerComponents method, which
 * allows one to give a customized container.
 *
 * @author Guilherme Silveira
 */
public class PicoProvider implements ContainerProvider {

	private final MutablePicoContainer picoContainer;
    private MutablePicoContainer childContainer;
    private final ThreadLocal<Container> containersByThread = new ThreadLocal<Container>();

    private static final Logger logger = LoggerFactory.getLogger(PicoProvider.class);
	private final Container container;

    private final class AppScopedContainer implements Container {
		public <T> T instanceFor(Class<T> type) {
			Container container = containersByThread.get();
			if (container == null) {
				return picoContainer.getComponent(type);
			}
			return container.instanceFor(type);
		}

		public <T> boolean canProvide(Class<T> type) {
			return instanceFor(type) != null;
		}
	}
    public PicoProvider() {
        this.picoContainer = new DefaultPicoContainer(new Caching(),
                new JavaEE5LifecycleStrategy(new NullComponentMonitor()), null);

        ComponentFactoryRegistry componentFactoryRegistry = new DefaultComponentFactoryRegistry();
        PicoComponentRegistry componentRegistry = new PicoComponentRegistry(this.picoContainer, componentFactoryRegistry);

        this.picoContainer.addComponent(componentRegistry);
        this.picoContainer.addComponent(componentFactoryRegistry);

        container = new AppScopedContainer();
		picoContainer.addComponent(Container.class, container);
    }

    public final void start(ServletContext context) {
	    ComponentRegistry componentRegistry = getComponentRegistry();
	    registerBundledComponents(componentRegistry);

	    this.picoContainer.addComponent(context);
	    BasicConfiguration config = new BasicConfiguration(context);

	    // using the new vraptor.scan
	    WebAppBootstrap webAppBootstrap = new WebAppBootstrapFactory().create(config);
	    webAppBootstrap.configure(componentRegistry);

	    // call old-style custom components registration
	    registerCustomComponents(componentRegistry);

	    // start the container
	    getComponentRegistry().init();
	    picoContainer.start();
	    registerCacheComponents();

	    // call all handlers for registered components
    	Collection<Class<?>> components = getComponentRegistry().getAllRegisteredApplicationScopedComponents();
    	List<StereotypeHandler> handlers = picoContainer.getComponents(StereotypeHandler.class);

    	for (Class<?> type : components) {
	        for (StereotypeHandler handler : handlers) {
	    		if (type.isAnnotationPresent(handler.stereotype())) {
	    			handler.handle(type);
	    		}
	    	}
    	}
	}
    
    public Container getContainer() {
    	return container;
    }

    /**
     * Create a child container, and register cached components. This way, Cached components will use registered implementations
     * for their types, and will be used on dependency injection
     */
	private void registerCacheComponents() {
		PicoComponentRegistry registry = getComponentRegistry();
		this.childContainer = registry.makeChildContainer();

		Map<Class<?>, Class<?>> cachedComponents = BaseComponents.getCachedComponents();
		for (Entry<Class<?>, Class<?>> entry : cachedComponents.entrySet()) {
			registry.register(entry.getKey(), entry.getValue());
		}

		this.childContainer.start();
	}

	/**
	 * Register default vraptor-pico implementation components.
	 */
	protected void registerBundledComponents(ComponentRegistry registry) {
	    logger.debug("Registering base pico container related implementation components");
	    for (Class<? extends StereotypeHandler> entry : BaseComponents.getStereotypeHandlers()) {
			registry.register(entry, entry);
		}
	    registerAll(registry, BaseComponents.getApplicationScoped());
	    registerAll(registry, BaseComponents.getRequestScoped());
	    registerAll(registry, BaseComponents.getPrototypeScoped());
	    for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
	        registry.register(converterType, converterType);
	    }
	}

	private void registerAll(ComponentRegistry registry, Map<Class<?>, Class<?>> scope) {
		for (Map.Entry<Class<?>, Class<?>> entry : scope.entrySet()) {
	        registry.register(entry.getKey(), entry.getValue());
	        registry.register(entry.getValue(), entry.getValue());
	    }
	}

	protected void registerCustomComponents(ComponentRegistry registry) {
		/* TODO: For now, this is an empty hook method to enable subclasses to use
		 * the scanner and register their specific components.
		 *
		 * In the future, if we scan the classpath for StereotypeHandlers, we can
		 * eliminate this hook.
		 */
	}

	public void stop() {
	    picoContainer.stop();
	    picoContainer.dispose();
	}

	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
        PicoBasedContainer container = null;
        try {
            container = getComponentRegistry().provideRequestContainer(request);
            container.getContainer().start();

            containersByThread.set(container);
            return execution.insideRequest(container);
        } finally {
            if (container != null) {
                MutablePicoContainer picoContainer = container.getContainer();
                picoContainer.stop();
                picoContainer.dispose();
            }
            containersByThread.set(null);
        }
    }

    protected PicoComponentRegistry getComponentRegistry() {
    	return this.picoContainer.getComponent(PicoComponentRegistry.class);
    }
}
