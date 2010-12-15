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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer extends AbstractComponentRegistry implements Container {

	private static final Logger logger = LoggerFactory.getLogger(SpringBasedContainer.class);

    final List<Class<?>> toRegister = new ArrayList<Class<?>>();

	private final ConfigurableWebApplicationContext parentContext;

	private SpringRegistry registry;

	private final BasicConfiguration config;

    public SpringBasedContainer(ConfigurableWebApplicationContext parentContext, BasicConfiguration config) {
        this.parentContext = parentContext;
		this.config = config;

    }

    public void register(Class<?> requiredType, Class<?> componentType) {
    	if (parentContext.isActive()) {
    		this.registry.register(componentType);
		} else {
			toRegister.add(componentType);
		}
    }

    public <T> T instanceFor(Class<T> type) {
    	try {
			return parentContext.getBean(type);
		} catch (NoSuchBeanDefinitionException e) {
			Map<String, T> beans = parentContext.getBeansOfType(type);
			for (Entry<String, T> def : beans.entrySet()) {
				BeanDefinition definition = parentContext.getBeanFactory().getBeanDefinition(def.getKey());
				if (isPrimary(definition) || hasGreaterRoleThanInfrastructure(definition)) {
					return def.getValue();
				}
			}
			throw e;
		}
    }

    public <T> boolean canProvide(Class<T> type) {
    	return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(parentContext, type).length > 0;
    }

    public void start(ServletContext context) {
        parentContext.setServletContext(context);
        parentContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

			public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
				SpringBasedContainer.this.registry = new SpringRegistry(factory, SpringBasedContainer.this);

				registry.configure();

				registry.registerCustomComponents(toRegister);
				if (config.isClasspathScanningEnabled()) {
					scanWebInfClasses(factory);
					scanPackages(factory);
				} else {
					logger.info("Classpath scanning disabled");
				}

			}
		});
        parentContext.refresh();

        parentContext.start();
    }

    public void stop() {
        parentContext.stop();
        if (parentContext instanceof DisposableBean){
			try {
				((DisposableBean)parentContext).destroy();
			} catch (Exception e) {
				logger.error("Error when destroying application context", e);
			}
		}
    }


    private boolean isPrimary(BeanDefinition definition) {
		return definition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) definition).isPrimary();
	}

	private boolean hasGreaterRoleThanInfrastructure(BeanDefinition definition) {
		return definition.getRole() < BeanDefinition.ROLE_INFRASTRUCTURE;
	}

	private void scanPackages(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		if (config.hasBasePackages()) {
			logger.info("Scanning packages from WEB-INF/classes and jars: {}", Arrays.toString(config.getBasePackages()));

			ComponentScanner scanner = new ComponentScanner(configurableListableBeanFactory, this);
			scanner.scan(config.getBasePackages());
		}
	}

	private void scanWebInfClasses(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		String directory = config.getWebinfClassesDirectory();
		if (directory != null) {
			logger.info("Scanning WEB-INF/classes: {} ", directory);

			ComponentScanner scanner = new ComponentScanner(configurableListableBeanFactory, this);
			scanner.setResourcePattern("**/*.class");
			scanner.setResourceLoader(new WebinfClassesPatternResolver(config.getWebinfClassesDirectory()));
			scanner.scan("");
		} else {
			logger.warn("Cant invoke ServletContext.getRealPath. Some application servers, as WebLogic, must be configured to be able to do so." +
						" Or maybe your container is not exploding the war file. Not scanning WEB-INF/classes for VRaptor and Spring components.");
		}
	}
}
