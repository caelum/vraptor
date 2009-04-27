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

import javax.servlet.ServletContext;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultMethodParameters;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultRequestInfo;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.DefaultRequestParameters;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.resource.DefaultMethodLookupBuilder;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.validator.DefaultValidator;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;
import br.com.caelum.vraptor.view.jsp.PageResult;
import br.com.caelum.vraptor.vraptor2.RequestResult;

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
        this.container = new DefaultPicoContainer(new Caching(), new JavaEE5LifecycleStrategy(new NullComponentMonitor()), null);
        PicoContainersProvider containersProvider = new PicoContainersProvider(this.container);
        this.container.addComponent(containersProvider);
        registerComponents(getContainers());
        containersProvider.init();
        // TODO: cache
        // cache(CacheBasedResourceRegistry.class, ResourceRegistry.class);
        // cache(CacheBasedTypeCreator.class, AsmBasedTypeCreator.class);
    }

    /**
     * Register extra components that your app wants to.
     */
    protected void registerComponents(ComponentRegistry container) {
        singleInterfaceRegister(StupidTranslator.class, container);
        singleInterfaceRegister(DefaultResourceRegistry.class, container);
        singleInterfaceRegister(DefaultDirScanner.class, container);
        singleInterfaceRegister(WebInfClassesScanner.class, container);
        singleInterfaceRegister(DefaultInterceptorRegistry.class, container);
        singleInterfaceRegister(DefaultMethodLookupBuilder.class, container);
        singleInterfaceRegister(DefaultPathResolver.class, container);
        singleInterfaceRegister(ParanamerNameProvider.class, container);
        singleInterfaceRegister(DefaultConverters.class, container);
        singleInterfaceRegister(DefaultMethodParameters.class, container);
        singleInterfaceRegister(DefaultRequestParameters.class, container);
        singleInterfaceRegister(DefaultInterceptorStack.class, container);
        singleInterfaceRegister(DefaultRequestExecution.class, container);
        singleInterfaceRegister(DefaultResult.class, container);
        singleInterfaceRegister(OgnlParametersProvider.class, container);
        singleInterfaceRegister(DefaultRequestInfo.class, container);
        singleInterfaceRegister(DefaultValidator.class, container);

        container.register(ForwardToDefaultViewInterceptor.class, ForwardToDefaultViewInterceptor.class);
        container.register(LogicResult.class, DefaultLogicResult.class);
        container.register(RequestResult.class, RequestResult.class);
        container.register(PageResult.class, DefaultPageResult.class);
        container.register(TypeCreator.class, AsmBasedTypeCreator.class);
        container.register(EmptyElementsRemoval.class, EmptyElementsRemoval.class);
        container.register(ParametersInstantiatorInterceptor.class, ParametersInstantiatorInterceptor.class);
        container.register(InterceptorListPriorToExecutionExtractor.class, InterceptorListPriorToExecutionExtractor.class);
        container.register(URLParameterExtractorInterceptor.class, URLParameterExtractorInterceptor.class);
        container.register(ResourceLookupInterceptor.class, ResourceLookupInterceptor.class);
        container.register(InstantiateInterceptor.class, InstantiateInterceptor.class);
        container.register(ExecuteMethodInterceptor.class, ExecuteMethodInterceptor.class);
    }

    private void singleInterfaceRegister(Class<?> type, ComponentRegistry registry) {
        Class<?>[] interfaces = type.getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalArgumentException(
                    "Invalid registering of a type with more than one interface" +
                            " being registered as a single interface component: "
                            + type.getName());
        }
        registry.register(interfaces[0], type);
    }

    public <T> T provideForRequest(VRaptorRequest request, Execution<T> execution) {
        return execution.insideRequest(getContainers().provide(request));
    }

    public void start(ServletContext context) {
        this.container.addComponent(context);
        container.getComponent(ResourceLocator.class).loadAll();
        container.start();
    }

    public void stop() {
        container.stop();
        container.dispose();
    }

    private PicoContainersProvider getContainers() {
        return this.container.getComponent(PicoContainersProvider.class);
    }

}
