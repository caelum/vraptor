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
package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.GenericWebApplicationContext;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultMethodParameters;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultRequestInfo;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.RequestExecution;
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
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultMethodLookupBuilder;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer implements Container, ComponentRegistry {
    private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    private final GenericWebApplicationContext applicationContext;

    private String[] basePackages = {"br.com.caelum.vraptor"};

    public SpringBasedContainer(String... basePackages) {
        // TODO provide users the ability to provide custom containers
        if (basePackages.length > 0) {
            this.basePackages = basePackages;
        }
        applicationContext = new GenericWebApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(applicationContext);
        registerCustomInjectionProcessor(applicationContext);
    }

    private void registerCustomInjectionProcessor(GenericApplicationContext applicationContext) {
        RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        definition.getPropertyValues().addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
        applicationContext.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
    }

    public void start(ServletContext context) {
        registerInstanceFor(ServletContext.class, context);
        registerApplicationScopedComponents();
        registerRequestScopedComponents();

        new ComponentScanner(applicationContext).scan(basePackages);
        applicationContext.refresh();
        applicationContext.start();
    }

    public void stop() {
        applicationContext.stop();
        applicationContext.destroy();
    }

    private void registerApplicationScopedComponents() {
        register(DefaultResourceRegistry.class);
        register(StupidTranslator.class);
        register(DefaultInterceptorRegistry.class);
        register(AsmBasedTypeCreator.class);
        register(DefaultMethodLookupBuilder.class);
        register(DefaultPathResolver.class);
        register(ParanamerNameProvider.class);
    }

    private void register(Class<?> type) {
        register(type, type);
    }

    private void registerRequestScopedComponents() {
        register(ParametersInstantiatorInterceptor.class);
        register(DefaultMethodParameters.class);
        register(DefaultRequestParameters.class);
        register(InterceptorListPriorToExecutionExtractor.class);
        register(URLParameterExtractorInterceptor.class);
        register(DefaultInterceptorStack.class);
        register(DefaultRequestExecution.class);
        register(ResourceLookupInterceptor.class);
        register(InstantiateInterceptor.class);
        register(DefaultResult.class);
        register(ExecuteMethodInterceptor.class);
        register(DefaultPageResult.class);
        register(OgnlParametersProvider.class);
        register(DefaultConverters.class);
        register(DefaultRequestInfo.class);
        register(HttpServletRequestProvider.class);
        register(HttpServletResponseProvider.class);
        register(VRaptorRequestProvider.class);
        registerInstanceFor(Container.class, this);
        registerInstanceFor(ComponentRegistry.class, this);
    }

    @SuppressWarnings("unchecked")
    public <T> T instanceFor(Class<T> type) {
        T instance = (T) BeanFactoryUtils.beanOfType(applicationContext, type);
        return wrapWhenNeeded(type, instance);
    }

    @SuppressWarnings("unchecked")
    private <T> T wrapWhenNeeded(Class<T> type, T instance) {
        if (RequestExecution.class.isAssignableFrom(type)) {
            VRaptorRequest request = instanceFor(VRaptorRequest.class);
            RequestExecution execution = (RequestExecution) instance;
            ServletContext context = instanceFor(ServletContext.class);
            return (T) new RequestExecutionWrapper(request, execution, context);
        }
        return instance;
    }

    private <T> void registerInstanceFor(Class<? super T> resolvableType, T instance) {
        applicationContext.getBeanFactory().registerResolvableDependency(resolvableType, instance);
    }

    public void register(Class<?> requiredType, Class<?> type) {
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
        String name = beanNameGenerator.generateBeanName(definition, applicationContext);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definition, name);

        VRaptorScopeResolver scopeResolver = new VRaptorScopeResolver();
        ScopeMetadata scopeMetadata = scopeResolver.resolveScopeMetadata(definition);
        definitionHolder = applyScope(definitionHolder, scopeMetadata);

        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, applicationContext);
    }

    /**
     * From org.springframework.context.annotation.ClassPathBeanDefinitionScanner#applyScope()
     *
     * @param definition
     * @param scopeMetadata
     * @return
     */
    private BeanDefinitionHolder applyScope(BeanDefinitionHolder definition, ScopeMetadata scopeMetadata) {
        String scope = scopeMetadata.getScopeName();
        ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
        definition.getBeanDefinition().setScope(scope);
        if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope) ||
                proxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        } else {
            boolean proxyTargetClass = proxyMode.equals(ScopedProxyMode.TARGET_CLASS);
            return ScopedProxyUtils.createScopedProxy(definition, applicationContext, proxyTargetClass);
        }
    }
}
