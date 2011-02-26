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
import java.util.Set;
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

import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.collect.Sets;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer extends AbstractComponentRegistry implements Container {

	private static final class BeanRegistrationProcessor implements BeanFactoryPostProcessor {
		private final SpringBasedContainer container;

		public BeanRegistrationProcessor(SpringBasedContainer container) {
			this.container = container;
		}

		public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
			SpringRegistry registry = new SpringRegistry(factory, container);

			registry.configure();

			registry.registerCustomComponents(container.toRegister);
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(SpringBasedContainer.class);

    final Set<Class<?>> toRegister = Sets.newHashSet();

	private final ConfigurableWebApplicationContext parentContext;

    public SpringBasedContainer(ConfigurableWebApplicationContext parentContext) {
        this.parentContext = parentContext;
    }

    public void register(Class<?> requiredType, Class<?> componentType) {
    	if (parentContext.isActive() && "VRaptor".equals(parentContext.getId())) {
    		logger.info("registering class {} to {} after container initialization. Please avoid this", requiredType, componentType);
    		new SpringRegistry(parentContext.getBeanFactory(), this).register(componentType);
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
        parentContext.addBeanFactoryPostProcessor(new BeanRegistrationProcessor(this));
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
}
