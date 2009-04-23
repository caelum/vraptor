package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.PostConstruct;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class ResourceRegistrar implements BeanPostProcessor {
    private final ResourcesHolder resourcesHolder;
    private final ResourceRegistry resourceRegistry;
    private boolean run;

    public ResourceRegistrar(ResourcesHolder resourcesHolder, ResourceRegistry resourceRegistry) {
        this.resourcesHolder = resourcesHolder;
        this.resourceRegistry = resourceRegistry;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!run) {
            run = true;
            resourcesHolder.registerAllOn(resourceRegistry);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
