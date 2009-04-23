package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class ResourceRegistrar implements MergedBeanDefinitionPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceRegistrar.class);
    private final ResourceRegistry registry;

    public ResourceRegistrar(ResourceRegistry registry) {
        this.registry = registry;
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class beanType, String beanName) {
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            AnnotationMetadata metadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
            if (metadata.hasMetaAnnotation(Stereotype.class.getName())) {
                LOGGER.info("found annotated component or resource: " + beanType);
                registry.register(new DefaultResource(beanType));
            }
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
