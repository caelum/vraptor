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
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.extra.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.http.ognl.EmptyElementsRemoval;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.view.DefaultPageResult;
import br.com.caelum.vraptor.view.EmptyResult;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

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
        PicoContainersProvider containersProvider = new PicoContainersProvider(this.container);
        this.container.addComponent(containersProvider);
    }

    /**
     * Register extra components that your app wants to.
     */
    protected void registerBundledComponents(ComponentRegistry container) {
        logger.debug("Registering base pico container related implementation components");
        for (Class<?> type : BaseComponents.getApplicationScoped()) {
            singleInterfaceRegister(type, container);
        }
        for (Class<?> type : BaseComponents.getRequestScoped()) {
            singleInterfaceRegister(type, container);
        }
        container.register(MultipartConfig.class, DefaultMultipartConfig.class);
        container.register(ForwardToDefaultViewInterceptor.class, ForwardToDefaultViewInterceptor.class);
        container.register(LogicResult.class, DefaultLogicResult.class);
        container.register(PageResult.class, DefaultPageResult.class);
        container.register(EmptyResult.class, EmptyResult.class);
        container.register(OutjectResult.class, OutjectResult.class);
        container.register(TypeCreator.class, AsmBasedTypeCreator.class);
        container.register(EmptyElementsRemoval.class, EmptyElementsRemoval.class);
        container.register(ParametersInstantiatorInterceptor.class, ParametersInstantiatorInterceptor.class);
        container.register(InterceptorListPriorToExecutionExtractor.class,
                InterceptorListPriorToExecutionExtractor.class);
        container.register(DownloadInterceptor.class, DownloadInterceptor.class);
        container.register(MultipartInterceptor.class, MultipartInterceptor.class);
        container.register(URLParameterExtractorInterceptor.class, URLParameterExtractorInterceptor.class);
        container.register(ResourceLookupInterceptor.class, ResourceLookupInterceptor.class);
        container.register(InstantiateInterceptor.class, InstantiateInterceptor.class);
        container.register(ExecuteMethodInterceptor.class, ExecuteMethodInterceptor.class);
        container.register(ResourceRegistrar.class, ResourceRegistrar.class);
        container.register(InterceptorRegistrar.class, InterceptorRegistrar.class);
        container.register(ConverterRegistrar.class, ConverterRegistrar.class);
        container.register(ComponentFactoryRegistry.class, DefaultComponentFactoryRegistry.class);
        container.register(ComponentFactoryRegistrar.class, ComponentFactoryRegistrar.class);
    }

    private void singleInterfaceRegister(Class<?> type, ComponentRegistry registry) {
        Class<?>[] interfaces = type.getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalArgumentException("Invalid registering of a type with more than one interface"
                    + " being registered as a single interface component: " + type.getName());
        }
        registry.register(interfaces[0], type);
    }

    public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
        PicoBasedContainer container = null;
        try {
            container = getContainers().provide(request);
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

    protected PicoContainersProvider getContainers() {
        return this.container.getComponent(PicoContainersProvider.class);
    }

    protected MutablePicoContainer getContainer() {
        return container;
    }
}
