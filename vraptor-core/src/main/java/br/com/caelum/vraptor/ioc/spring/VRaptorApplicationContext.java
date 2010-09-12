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

import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

import com.google.common.collect.MapMaker;

/**
 * @author Fabio Kung
 */
public class VRaptorApplicationContext extends AbstractRefreshableWebApplicationContext {

	private static final Logger logger = LoggerFactory.getLogger(VRaptorApplicationContext.class);

	public static final String RESOURCES_LIST = "br.com.caelum.vraptor.resources.list";

	private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
	private final SpringBasedContainer container;
	private final BasicConfiguration config;

	private Map<Class<?>, String> typeToBeanName = new MapMaker().weakValues().makeMap();

	public VRaptorApplicationContext(SpringBasedContainer container, BasicConfiguration config) {
		this.container = container;
		this.config = config;
	}

	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		WebApplicationContextUtils.registerWebApplicationScopes(beanFactory);
	}

	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
		if (getParent() == null || getParent().getBeanNamesForType(ServletContext.class).length == 0) {
			beanFactory.registerSingleton(ServletContext.class.getName(), config.getServletContext());
		// beanFactory.ignoreDependencyType(ServletContext.class);
		}

		registerApplicationScopedComponentsOn(beanFactory);
		registerRequestScopedComponentsOn(beanFactory);
		registerPrototypeScopedComponentsOn(beanFactory);
		registerCustomComponentsOn(beanFactory);

		{
			String directory = config.getWebinfClassesDirectory();
			if (directory != null) {
				logger.info("Scanning WEB-INF/classes: " + directory);
				ComponentScanner scanner = new ComponentScanner(beanFactory, container);
				scanner.setResourcePattern("**/*.class");
				scanner.setResourceLoader(new WebinfClassesPatternResolver(config.getWebinfClassesDirectory()));
				scanner.scan("");
			} else {
				logger
						.warn("Cant invoke ServletContext.getRealPath. Some application servers, as WebLogic, must be configured to be able to do so." +
								" Or maybe your container is not exploding the war file.Not scanning WEB-INF/classes for VRaptor and Spring components.");
			}

		}
		if (config.hasBasePackages()) {
			ComponentScanner scanner = new ComponentScanner(beanFactory, container);
			logger
					.info("Scanning packages from WEB-INF/classes and jars: "
							+ Arrays.toString(config.getBasePackages()));
			scanner.scan(config.getBasePackages());
		}

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

		config.getServletContext();
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
	}

	private void register(final Class<?> type, ConfigurableListableBeanFactory beanFactory) {
		registerOn((BeanDefinitionRegistry) beanFactory, type, true);
		registerFactory(type, beanFactory);
	}

	@SuppressWarnings("unchecked")
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
	 * From
	 * org.springframework.context.annotation.ClassPathBeanDefinitionScanner
	 * #applyScope()
	 *
	 * @param definition
	 * @param scopeMetadata
	 * @return
	 */
	private BeanDefinitionHolder applyScopeOn(BeanDefinitionRegistry registry, BeanDefinitionHolder definition,
			ScopeMetadata scopeMetadata) {
		String scope = scopeMetadata.getScopeName();
		ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
		definition.getBeanDefinition().setScope(scope);
		if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope)
				|| proxyMode.equals(ScopedProxyMode.NO)) {
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
		if (!typeToBeanName.containsKey(type)) {
			logger.debug("Cache miss for {}", type);
			String name = compatibleNameFor(type);
			typeToBeanName.put(type, name);
		}
		return type.cast(getBean(typeToBeanName.get(type)));
	}

	private String compatibleNameFor(Class<?> type) {
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, type);
		if (names.length == 0) {
			throw new NoSuchBeanDefinitionException(type, "no bean for this type registered");
		} else if (names.length == 1) {
			return names[0];
		} else {
			for (String name : names) {
				BeanDefinition definition = getBeanFactory().getBeanDefinition(name);
				if (isPrimary(definition) || hasGreaterRoleThanInfrastructure(definition)) {
					return name;
				}
			}
			throw new NoSuchBeanDefinitionException("there are " + names.length + " implementations for the type ["
					+ type
					+ "], but none of them is primary or has a Role greater than BeanDefinition.ROLE_INFRASTRUCTURE");
		}
	}


	private boolean isPrimary(BeanDefinition definition) {
		return definition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) definition).isPrimary();
	}

	private boolean hasGreaterRoleThanInfrastructure(BeanDefinition definition) {
		return definition.getRole() < BeanDefinition.ROLE_INFRASTRUCTURE;
	}
}
