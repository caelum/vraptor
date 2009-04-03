package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @author Fabio Kung
 */
public class ComponentScanner extends ClassPathBeanDefinitionScanner {
    public ComponentScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        addIncludeFilter(new ComponentTypeFilter());
//        setScopeMetadataResolver(new VRaptorScopeResolver());
    }
}
