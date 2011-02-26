/**
 * 
 */
package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

public class UniqueBeanNameGenerator implements BeanNameGenerator {

	private final BeanNameGenerator delegate;

	public UniqueBeanNameGenerator(BeanNameGenerator delegate) {
		this.delegate = delegate;
	}

	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		String name = delegate.generateBeanName(definition, registry);
		while (registry.containsBeanDefinition(name) &&
				!registry.getBeanDefinition(name).getBeanClassName().equals(definition.getBeanClassName())) {
			name = name + "$";
		}
		return name;
	}
}