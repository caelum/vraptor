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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class StereotypedBeansRegistrar implements ApplicationListener<ApplicationEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StereotypedBeansRegistrar.class);
	private final List<StereotypeHandler> stereotypeHandlers;

    public StereotypedBeansRegistrar(List<StereotypeHandler> stereotypeHandlers) {
		this.stereotypeHandlers = stereotypeHandlers;
    }

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			handleRefresh(((ContextRefreshedEvent) event).getApplicationContext());
		}
	}

	private void handleRefresh(ApplicationContext beanFactory) {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String name : beanDefinitionNames) {
			Class<?> beanType = beanFactory.getType(name);
			LOGGER.debug("scanning {} for bean definition {}", beanType, name);
			if (beanType == null) {
				LOGGER.info("null type for bean {}", name);
				continue;
			}

			for (StereotypeHandler handler : stereotypeHandlers) {
				LOGGER.trace("scanning {} with {}", beanType, handler);
				if (beanType.isAnnotationPresent(handler.stereotype())) {
					handler.handle(beanType);
				}
			}
		}
	}
}
