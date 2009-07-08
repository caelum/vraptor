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

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Managing internal components by using pico container.<br>
 * There is an extension point through the registerComponents method, which
 * allows one to give a customized container.
 *
 * @author Guilherme Silveira
 */
public class PicoProvider implements ContainerProvider {

    private final MutablePicoContainer container;

    private static final Logger logger = LoggerFactory.getLogger(PicoProvider.class);

    public PicoProvider() {
        this.container = new DefaultPicoContainer(new Caching(),
                new JavaEE5LifecycleStrategy(new NullComponentMonitor()), null);

        ComponentFactoryRegistry componentFactoryRegistry = new DefaultComponentFactoryRegistry();
        PicoComponentRegistry componentRegistry = new PicoComponentRegistry(this.container, componentFactoryRegistry);

        this.container.addComponent(componentRegistry);
        this.container.addComponent(componentFactoryRegistry);
    }

    /**
     * Register extra components that your app wants to.
     */
    protected void registerBundledComponents(ComponentRegistry container) {
        logger.debug("Registering base pico container related implementation components");
        for (Map.Entry<Class<?>, Class<?>> entry : BaseComponents.getApplicationScoped().entrySet()) {
            container.register(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Class<?>, Class<?>> entry : BaseComponents.getRequestScoped().entrySet()) {
            container.register(entry.getKey(), entry.getValue());
        }
        for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
            container.register(converterType, converterType);
        }

        container.register(ResourceRegistrar.class, ResourceRegistrar.class);
        container.register(InterceptorRegistrar.class, InterceptorRegistrar.class);
        container.register(ConverterRegistrar.class, ConverterRegistrar.class);
        container.register(ComponentFactoryRegistrar.class, ComponentFactoryRegistrar.class);
    }

    public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
        PicoBasedContainer container = null;
        try {
            container = getContainers().provideRequestContainer(request);
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

    public void start(ServletContext context) {
        ComponentRegistry components = getContainers();
        registerBundledComponents(components);

        this.container.addComponent(context);

        Scanner scanner = new ReflectionsScanner(context);
        new ComponentRegistrar(components).registerFrom(scanner);

        getContainers().init();

        container.getComponent(ResourceRegistrar.class).registerFrom(scanner);
        container.getComponent(InterceptorRegistrar.class).registerFrom(scanner);
        container.getComponent(ConverterRegistrar.class).registerFrom(scanner);
        container.getComponent(ComponentFactoryRegistrar.class).registerFrom(scanner);

        container.start();
    }

    public void stop() {
        container.stop();
        container.dispose();
    }

    protected PicoComponentRegistry getContainers() {
        return this.container.getComponent(PicoComponentRegistry.class);
    }

    protected MutablePicoContainer getContainer() {
        return container;
    }
}
