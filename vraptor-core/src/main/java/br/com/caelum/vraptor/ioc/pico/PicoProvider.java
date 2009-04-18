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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.RegisterContainer;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultMethodParameters;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultRequestInfo;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.DefaultRequestParameters;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ParametersInstantiator;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.resource.DefaultMethodLookupBuilder;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

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
        this.container = new PicoBuilder().withCaching().build();
        this.container.addComponent(new PicoContainersProvider(this.container));
        for (Class<?> componentType : getCoreComponents()) {
            container.addComponent(componentType);
        }
        registerComponents(getContainers());
        // TODO
        // cache(CacheBasedResourceRegistry.class, ResourceRegistry.class);
        // cache(CacheBasedTypeCreator.class, AsmBasedTypeCreator.class);
    }

    private PicoContainersProvider getContainers() {
        return this.container.getComponent(PicoContainersProvider.class);
    }

    /**
     * Register extra components that your app wants to.
     */
    protected void registerComponents(RegisterContainer container) {
        for (Class<?> type : getChildComponentTypes()) {
            container.register(type);
        }
    }

    /**
     * While extending pico provider, do not register any INSTANCE component!
     * Cached instances might give problems later on.<br>
     * If there is any component instantiated and we change the implementation,
     * those who access the previous implementation will keep the reference
     * while new components will reference the new one -> NASTY!
     */
    protected List<Class<?>> getCoreComponents() {
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.add(StupidTranslator.class);
        components.add(DefaultResourceRegistry.class);
        components.add(DefaultDirScanner.class);
        components.add(WebInfClassesScanner.class);
        components.add(DefaultInterceptorRegistry.class);
        components.add(AsmBasedTypeCreator.class);
        components.add(DefaultMethodLookupBuilder.class);
        components.add(DefaultPathResolver.class);
        components.add(ParanamerNameProvider.class);
        return components;
    }

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

    public void start(ServletContext context) {
        this.container.addComponent(context);
        instanceFor(ResourceLocator.class).loadAll();
        container.start();
    }

    public void stop() {
        container.stop();
    }

    public Container provide(VRaptorRequest request) {
        return getContainers().provide(request);
    }

    protected List<Class<?>> getChildComponentTypes() {
        // TODO remove and replace by invoking register on registercontainer
        // only if interfaces were not registered
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.add(ParametersInstantiator.class);
        components.add(DefaultMethodParameters.class);
        components.add(DefaultRequestParameters.class);
        components.add(InterceptorListPriorToExecutionExtractor.class);
        components.add(URLParameterExtractorInterceptor.class);
        components.add(DefaultInterceptorStack.class);
        components.add(DefaultRequestExecution.class);
        components.add(ResourceLookupInterceptor.class);
        components.add(InstantiateInterceptor.class);
        components.add(DefaultResult.class);
        components.add(ExecuteMethodInterceptor.class);
        components.add(DefaultPageResult.class);
        components.add(OgnlParametersProvider.class);
        components.add(DefaultConverters.class);
        components.add(DefaultRequestInfo.class);
        return components;
    }

}
