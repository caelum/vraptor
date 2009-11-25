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

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

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

    @Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        beanFactory.registerSingleton(ServletContext.class.getName(), getServletContext());
        beanFactory.ignoreDependencyType(ServletContext.class);
        registerApplicationScopedComponentsOn(beanFactory);
        registerRequestScopedComponentsOn(beanFactory);
        registerPrototypeScopedComponentsOn(beanFactory);
        registerCustomComponentsOn(beanFactory);

        new ComponentScanner(beanFactory, container).scan(basePackages);

        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
        registerCustomInjectionProcessor(beanFactory);
        registerCachedComponentsOn(beanFactory);
    }

    private void registerCustomComponentsOn(DefaultListableBeanFactory beanFactory) {
    	for (Class<?> type : container.getToRegister()) {
			register(type, beanFactory);
		}
	}

    private void registerPrototypeScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
    	for (Class<?> prototypeComponent : BaseComponents.getPrototypeScoped().values()) {
    		registerOn(beanFactory, prototypeComponent);
    	}
    }
	private void registerCachedComponentsOn(DefaultListableBeanFactory beanFactory) {
        for (Class<?> cachedComponent : BaseComponents.getCachedComponents().values()) {
			registerOn(beanFactory, cachedComponent, true);
		}
	}

	private void registerApplicationScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        for (Class<?> type : BaseComponents.getApplicationScoped().values()) {
            registerOn(beanFactory, type);
            registerFactory(type, beanFactory);
        }

        for (Class<? extends StereotypeHandler> handlerType : BaseComponents.getStereotypeHandlers()) {
			registerOn(beanFactory, handlerType);
		}

        registerOn(beanFactory, StereotypedBeansRegistrar.class);
        registerOn(beanFactory, DefaultSpringLocator.class);
    }

    private void registerRequestScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        for (Class<?> type : BaseComponents.getRequestScoped().values()) {
            registerOn(beanFactory, type);
            registerFactory(type, beanFactory);
        }

        for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
	        registerOn(beanFactory, converterType);
	    }

        registerOn(beanFactory, VRaptorRequestProvider.class, true);
        registerOn(beanFactory, HttpServletRequestProvider.class, true);
        registerOn(beanFactory, HttpServletResponseProvider.class, true);
        registerOn(beanFactory, HttpSessionProvider.class, true);

        beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);

        try {
			Class.forName("org.joda.time.LocalDate");
			registerOn(beanFactory, LocalDateConverter.class);
			registerOn(beanFactory, LocalTimeConverter.class);
		} catch (ClassNotFoundException e) {
			//OK, only register jodatime converters if jodatime is imported
		}
    }

    private void register(final Class<?> type, ConfigurableListableBeanFactory beanFactory) {
        registerOn((BeanDefinitionRegistry) beanFactory, type, true);
        registerFactory(type, beanFactory);
    }

	private void registerFactory(final Class<?> type, ConfigurableListableBeanFactory beanFactory) {
		if (ComponentFactory.class.isAssignableFrom(type)) {
            beanFactory.registerSingleton(type.getName(), new ComponentFactoryBean(container, type));
        }
	}
    public void register(Class<?> type) {
    	register(type, getBeanFactory());
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
