package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.resource.DefaultResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class ResourceFinder implements BeanFactoryPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceFinder.class);
    private ApplicationContext applicationContext;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] definitionNames = beanFactory.getBeanDefinitionNames();
        for (String name : definitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            LOGGER.info("scanning definition: " + beanDefinition + ", name: " + name +
                    ", to see if it is a Resource candidate");
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata metadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
                if (metadata.hasMetaAnnotation(Stereotype.class.getName())) {
                    ResourcesHolder resourcesHolder = (ResourcesHolder)
                            applicationContext.getBean(VRaptorApplicationContext.RESOURCES_LIST);
                    LOGGER.info("found annotated component or resource: " + beanDefinition.getBeanClassName());
                    resourcesHolder.add(new DefaultResource(beanFactory.getType(name)));
                }
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
