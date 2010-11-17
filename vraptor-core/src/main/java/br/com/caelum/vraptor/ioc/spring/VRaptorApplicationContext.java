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
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * @author Fabio Kung
 */
public class VRaptorApplicationContext extends AbstractRefreshableWebApplicationContext {

	private static final Logger logger = LoggerFactory.getLogger(VRaptorApplicationContext.class);

	public static final String RESOURCES_LIST = "br.com.caelum.vraptor.resources.list";

	private final SpringBasedContainer container;
	private final BasicConfiguration config;

	private final SpringRegistry registry;

	public VRaptorApplicationContext(SpringBasedContainer container, BasicConfiguration config, SpringRegistry registry) {
		this.container = container;
		this.config = config;
		this.registry = registry;
	}

	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		WebApplicationContextUtils.registerWebApplicationScopes(beanFactory);
	}

	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
		if (getParent() == null || getParent().getBeanNamesForType(ServletContext.class).length == 0) {
			beanFactory.registerSingleton(ServletContext.class.getName(), config.getServletContext());
		// beanFactory.ignoreDependencyType(ServletContext.class);
		}

		registry.registerVRaptorComponents(beanFactory);

		registerCustomComponentsOn(beanFactory);

		if (config.isClasspathScanningEnabled()) {
			scanWebInfClasses(beanFactory);
			scanPackages(beanFactory);
		} else {
			logger.info("Classpath scanning disabled");
		}

		AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
		registerCustomInjectionProcessor(beanFactory);
		registry.registerCachedComponentsOn(beanFactory);
	}

	private void scanPackages(DefaultListableBeanFactory beanFactory) {
		if (config.hasBasePackages()) {
			logger.info("Scanning packages from WEB-INF/classes and jars: {}", Arrays.toString(config.getBasePackages()));

			ComponentScanner scanner = new ComponentScanner(beanFactory, container);
			scanner.scan(config.getBasePackages());
		}
	}

	private void scanWebInfClasses(DefaultListableBeanFactory beanFactory) {
		String directory = config.getWebinfClassesDirectory();
		if (directory != null) {
			logger.info("Scanning WEB-INF/classes: {} ", directory);

			ComponentScanner scanner = new ComponentScanner(beanFactory, container);
			scanner.setResourcePattern("**/*.class");
			scanner.setResourceLoader(new WebinfClassesPatternResolver(config.getWebinfClassesDirectory()));
			scanner.scan("");
		} else {
			logger.warn("Cant invoke ServletContext.getRealPath. Some application servers, as WebLogic, must be configured to be able to do so." +
						" Or maybe your container is not exploding the war file. Not scanning WEB-INF/classes for VRaptor and Spring components.");
		}
	}

	private void registerCustomComponentsOn(DefaultListableBeanFactory beanFactory) {
		for (Class<?> type : container.getToRegister()) {
			registry.register(type, beanFactory);
		}
	}

	public void register(Class<?> type) {
		registry.register(type, getBeanFactory());
	}

	private void registerCustomInjectionProcessor(BeanDefinitionRegistry registry) {
		RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
		definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		definition.getPropertyValues().addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
		registry.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
	}

	@Override
	public <T> T getBean(Class<T> type) {
		try {
			return super.getBean(type);
		} catch (NoSuchBeanDefinitionException e) {
			Map<String, T> beans = getBeansOfType(type);
			for (Entry<String, T> def : beans.entrySet()) {
				BeanDefinition definition = getBeanFactory().getBeanDefinition(def.getKey());
				if (isPrimary(definition) || hasGreaterRoleThanInfrastructure(definition)) {
					return def.getValue();
				}
			}
			throw e;
		}
	}

	private boolean isPrimary(BeanDefinition definition) {
		return definition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) definition).isPrimary();
	}

	private boolean hasGreaterRoleThanInfrastructure(BeanDefinition definition) {
		return definition.getRole() < BeanDefinition.ROLE_INFRASTRUCTURE;
	}
}
