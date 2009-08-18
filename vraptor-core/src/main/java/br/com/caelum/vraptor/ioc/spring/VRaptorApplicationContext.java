/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.ioc.spring;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.extra.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.http.DefaultResourceTranslator;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.view.DefaultAcceptHeaderToFormat;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.view.DefaultPageResult;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.EmptyResult;

/**
 * @author Fabio Kung
 */
public class VRaptorApplicationContext extends AbstractRefreshableWebApplicationContext {
    public static final String RESOURCES_LIST = "br.com.caelum.vraptor.resources.list";

    private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    private final String[] basePackages;
    private final SpringBasedContainer container;

    public VRaptorApplicationContext(SpringBasedContainer container, String... basePackages) {
        this.container = container;
        this.basePackages = basePackages;
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory);
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        beanFactory.registerSingleton(ServletContext.class.getName(), getServletContext());
        beanFactory.ignoreDependencyType(ServletContext.class);
        registerApplicationScopedComponentsOn(beanFactory);
        registerRequestScopedComponentsOn(beanFactory);

        new ComponentScanner(beanFactory, container).scan(basePackages);

        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
        registerCustomInjectionProcessor(beanFactory);
    }

    private void registerApplicationScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        registerOn(beanFactory, DefaultRouter.class);
        registerOn(beanFactory, DefaultResourceTranslator.class);
        registerOn(beanFactory, DefaultInterceptorRegistry.class);
        registerOn(beanFactory, AsmBasedTypeCreator.class);
        registerOn(beanFactory, DefaultProxifier.class);
        registerOn(beanFactory, DefaultAcceptHeaderToFormat.class);
        registerOn(beanFactory, DefaultPathResolver.class);
        registerOn(beanFactory, ParanamerNameProvider.class);
        registerOn(beanFactory, DefaultConverters.class);
        for (Class<?> type : BaseComponents.getApplicationScoped().values()) {
            registerOn(beanFactory, type);
        }
        
        for (Class<? extends StereotypeHandler> handlerType : BaseComponents.getStereotypeHandlers()) {
			registerOn(beanFactory, handlerType);
		}
        
        registerOn(beanFactory, StereotypedBeansRegistrar.class);
        registerOn(beanFactory, DefaultMultipartConfig.class);
    }

    private void registerRequestScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        for (Class<?> type : BaseComponents.getRequestScoped().values()) {
            registerOn(beanFactory, type);
        }
        registerOn(beanFactory, AsmBasedTypeCreator.class);
        registerOn(beanFactory, ParametersInstantiatorInterceptor.class);
        registerOn(beanFactory, InterceptorListPriorToExecutionExtractor.class);
        registerOn(beanFactory, URLParameterExtractorInterceptor.class);
        registerOn(beanFactory, ResourceLookupInterceptor.class);
        registerOn(beanFactory, InstantiateInterceptor.class);
        registerOn(beanFactory, ExecuteMethodInterceptor.class);
        registerOn(beanFactory, DefaultPageResult.class);
        registerOn(beanFactory, ForwardToDefaultViewInterceptor.class);
        registerOn(beanFactory, DefaultLogicResult.class);
        registerOn(beanFactory, VRaptorRequestProvider.class, true);
        registerOn(beanFactory, HttpServletRequestProvider.class, true);
        registerOn(beanFactory, HttpServletResponseProvider.class, true);
        registerOn(beanFactory, HttpSessionProvider.class, true);
        registerOn(beanFactory, OutjectResult.class);
        registerOn(beanFactory, EmptyResult.class);
        registerOn(beanFactory, MultipartInterceptor.class);
        registerOn(beanFactory, DownloadInterceptor.class);

        beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);
    }

    public void register(final Class<?> type) {
        registerOn((BeanDefinitionRegistry) getBeanFactory(), type, true);
        if (ComponentFactory.class.isAssignableFrom(type)) {
            getBeanFactory().registerSingleton(type.getName(), new ComponentFactoryBean(container, type));
        }
    }

    private void registerOn(BeanDefinitionRegistry registry, Class<?> type) {
        registerOn(registry, type, false);
    }

    private void registerOn(BeanDefinitionRegistry registry, Class<?> type, boolean customComponent) {
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
        definition.setLazyInit(true);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        if (customComponent) {
            definition.setPrimary(true);
            definition.setRole(BeanDefinition.ROLE_APPLICATION);
        } else {
            definition.setPrimary(false);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        String name = beanNameGenerator.generateBeanName(definition, registry);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definition, name);

        VRaptorScopeResolver scopeResolver = new VRaptorScopeResolver();
        ScopeMetadata scopeMetadata = scopeResolver.resolveScopeMetadata(definition);
        definitionHolder = applyScopeOn(registry, definitionHolder, scopeMetadata);

        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * From org.springframework.context.annotation.ClassPathBeanDefinitionScanner#applyScope()
     *
     * @param definition
     * @param scopeMetadata
     * @return
     */
    private BeanDefinitionHolder applyScopeOn(BeanDefinitionRegistry registry, BeanDefinitionHolder definition, ScopeMetadata scopeMetadata) {
        String scope = scopeMetadata.getScopeName();
        ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
        definition.getBeanDefinition().setScope(scope);
        if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope) ||
                proxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        } else {
            boolean proxyTargetClass = proxyMode.equals(ScopedProxyMode.TARGET_CLASS);
            return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
        }
    }

    private void registerCustomInjectionProcessor(BeanDefinitionRegistry registry) {
        RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        definition.getPropertyValues().addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
        registry.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
    }

    public <T> T getBean(Class<T> type) {
        @SuppressWarnings("unchecked")
        Map<String, ? extends T> instances = BeanFactoryUtils.beansOfTypeIncludingAncestors(this, type);
        if (instances.size() == 0) {
            throw new NoSuchBeanDefinitionException(type, "no bean for this type registered");
        } else if (instances.size() == 1) {
            return instances.values().iterator().next();
        } else {
            for (Map.Entry<String, ? extends T> entry : instances.entrySet()) {
                BeanDefinition definition = getBeanFactory().getBeanDefinition(entry.getKey());
                if (isPrimary(definition)) {
                    return entry.getValue();
                } else if (hasGreaterRoleThanInfrastructure(definition)) {
                    return entry.getValue();
                }
            }
            throw new NoSuchBeanDefinitionException("there are " + instances.size() +
                    " implementations for the type [" + type +
                    "], but none of them is primary or has a Role greater than BeanDefinition.ROLE_INFRASTRUCTURE");
        }
    }

    private boolean isPrimary(BeanDefinition definition) {
        return definition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) definition).isPrimary();
    }

    private boolean hasGreaterRoleThanInfrastructure(BeanDefinition definition) {
        return definition.getRole() < BeanDefinition.ROLE_INFRASTRUCTURE;
    }
}
