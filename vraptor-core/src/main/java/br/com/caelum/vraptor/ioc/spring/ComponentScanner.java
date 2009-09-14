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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Fabio Kung
 */
class ComponentScanner extends ClassPathBeanDefinitionScanner {


	private static final Logger logger = LoggerFactory.getLogger(ComponentScanner.class);

	private final DefaultListableBeanFactory registry;
	private final Container container;

	public ComponentScanner(DefaultListableBeanFactory registry, Container container) {
        super(registry, false);
		this.registry = registry;
		this.container = container;
        addIncludeFilter(new ComponentTypeFilter());

        setScopeMetadataResolver(new VRaptorScopeResolver());
        setBeanNameGenerator(new UniqueBeanNameGenerator(new AnnotationBeanNameGenerator()));
    }

	public static class UniqueBeanNameGenerator implements BeanNameGenerator {

		private final BeanNameGenerator delegate;

		public UniqueBeanNameGenerator(BeanNameGenerator delegate) {
			this.delegate = delegate;
		}

		public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
			String name = delegate.generateBeanName(definition, registry);
			if (registry.containsBeanDefinition(name)) {
				name = name + "$";
			}
			return name;
		}

	}
    @Override
    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
    	super.postProcessBeanDefinition(beanDefinition, beanName);
    	beanDefinition.setPrimary(true);
		try {
			Class<?> componentType = Class.forName(beanDefinition.getBeanClassName());
			if (ComponentFactory.class.isAssignableFrom(componentType)) {
	            registry.registerSingleton(beanDefinition.getBeanClassName(), new ComponentFactoryBean(container, componentType));
	        }
		} catch (ClassNotFoundException e) {
			logger.debug("Class " + beanDefinition.getBeanClassName() + " was not found during bean definition proccess");
		}
    }

}
