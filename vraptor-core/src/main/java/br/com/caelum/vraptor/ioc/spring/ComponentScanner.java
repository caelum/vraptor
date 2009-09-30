
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
