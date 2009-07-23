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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.AnnotationMetadata;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class StereotypedBeansRegistrar implements ApplicationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StereotypedBeansRegistrar.class);
	private final List<StereotypeHandler> stereotypeHandlers;

    public StereotypedBeansRegistrar(List<StereotypeHandler> stereotypeHandlers) {
		this.stereotypeHandlers = stereotypeHandlers;
    }

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			ContextRefreshedEvent contextRefreshedEvent = (ContextRefreshedEvent) event;
			ConfigurableListableBeanFactory applicationContext = extractBeanFactory(contextRefreshedEvent);
			handleRefresh(applicationContext);
		}
	}

	private ConfigurableListableBeanFactory extractBeanFactory(ContextRefreshedEvent contextRefreshedEvent) {
		ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
		
		if (!(applicationContext instanceof ConfigurableApplicationContext))
			throw new VRaptorException("VRaptorApplicationContext must be a ConfigurableApplicationContext");
			
		ConfigurableApplicationContext configurableApplicationContext = 
			(ConfigurableApplicationContext) applicationContext;
		
		return configurableApplicationContext.getBeanFactory();
	}

	private void handleRefresh(ConfigurableListableBeanFactory beanFactory) {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String name : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
			
			LOGGER.debug("scanning definition: " + beanDefinition + ", to see if it is a candidate");
			
			for (StereotypeHandler handler : stereotypeHandlers) {
				if (beanCanBeHandledBy(beanDefinition, handler))
					handler.handle(beanFactory.getType(name));
			}
		}
	}
	
	private boolean beanCanBeHandledBy(BeanDefinition beanDefinition, StereotypeHandler stereotypeHandler) {
		if (!(beanDefinition instanceof AnnotatedBeanDefinition))
			return false;

		AnnotationMetadata metadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
		if (!metadata.hasAnnotation(stereotypeHandler.stereotype().getName()))
			return false;
		
		LOGGER.info("found component annotated with @" + stereotypeHandler.stereotype()  + ": " + beanDefinition.getBeanClassName());
		return true;
	}
}
