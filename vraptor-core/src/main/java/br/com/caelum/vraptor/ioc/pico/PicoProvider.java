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

import java.util.Map;

import javax.servlet.ServletContext;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * Managing internal components by using pico container.<br>
 * There is an extension point through the registerComponents method, which
 * allows one to give a customized container.
 *
 * @author Guilherme Silveira
 */
public class PicoProvider implements ContainerProvider {

    private final MutablePicoContainer picoContainer;

    private static final Logger logger = LoggerFactory.getLogger(PicoProvider.class);

    public PicoProvider() {
        this.picoContainer = new DefaultPicoContainer(new Caching(),
                new JavaEE5LifecycleStrategy(new NullComponentMonitor()), null);

        ComponentFactoryRegistry componentFactoryRegistry = new DefaultComponentFactoryRegistry();
        PicoComponentRegistry componentRegistry = new PicoComponentRegistry(this.picoContainer, componentFactoryRegistry);

        this.picoContainer.addComponent(componentRegistry);
        this.picoContainer.addComponent(componentFactoryRegistry);
    }

    public final void start(ServletContext context) {
	    ComponentRegistry componentRegistry = getComponentRegistry();
	    registerBundledComponents(componentRegistry);
	
	    this.picoContainer.addComponent(context);
	
	    Scanner scanner = new ReflectionsScanner(context);
	
	    getComponentRegistry().init();
	    
	    StereotypedComponentRegistrar componentRegistrar = picoContainer.getComponent(StereotypedComponentRegistrar.class);
	    componentRegistrar.registerFrom(scanner);
	    
	    registerCustomComponents(picoContainer, scanner);
	    
	    picoContainer.start();
	}

	/**
	 * Register extra components that your app wants to.
	 */
	protected void registerBundledComponents(ComponentRegistry registry) {
	    logger.debug("Registering base pico container related implementation components");
	    for (Class<? extends StereotypeHandler> entry : BaseComponents.getStereotypeHandlers()) {
			registry.register(entry, entry);
		}
	    registry.register(ComponentHandler.class, ComponentHandler.class);
	    for (Map.Entry<Class<?>, Class<?>> entry : BaseComponents.getApplicationScoped().entrySet()) {
	        registry.register(entry.getKey(), entry.getValue());
	    }
	    for (Map.Entry<Class<?>, Class<?>> entry : BaseComponents.getRequestScoped().entrySet()) {
	        registry.register(entry.getKey(), entry.getValue());
	    }
	    for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
	        registry.register(converterType, converterType);
	    }
	
	    registry.register(ResourceRegistrar.class, ResourceRegistrar.class);
	    registry.register(InterceptorRegistrar.class, InterceptorRegistrar.class);
	    registry.register(ConverterRegistrar.class, ConverterRegistrar.class);
	    registry.register(ComponentFactoryRegistrar.class, ComponentFactoryRegistrar.class);
	    registry.register(StereotypedComponentRegistrar.class, StereotypedComponentRegistrar.class);
	}

	protected void registerCustomComponents(PicoContainer picoContainer, Scanner scanner) {
		picoContainer.getComponent(ResourceRegistrar.class).registerFrom(scanner);
	    picoContainer.getComponent(InterceptorRegistrar.class).registerFrom(scanner);
	    picoContainer.getComponent(ConverterRegistrar.class).registerFrom(scanner);
	    picoContainer.getComponent(ComponentFactoryRegistrar.class).registerFrom(scanner);
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
            return execution.insideRequest(container);
        } finally {
            if (container != null) {
                MutablePicoContainer picoContainer = container.getContainer();
                picoContainer.stop();
                picoContainer.dispose();
            }
        }
    }

    protected PicoComponentRegistry getComponentRegistry() {
        return this.picoContainer.getComponent(PicoComponentRegistry.class);
    }
}
