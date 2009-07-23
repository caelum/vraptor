package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author: Fabio Kung
 */
public class ComponentFactoryBean<T extends ComponentFactory<Object>> implements FactoryBean {

    private Container container;

    private Class<T> factoryType;
    private Class<?> targetType;

    public ComponentFactoryBean(Container container, Class<T> factoryType) {
        this.container = container;
        this.factoryType = factoryType;
        this.targetType = new ComponentFactoryIntrospector().targetTypeForComponentFactory(factoryType);
    }

    public Object getObject() {
        return container.instanceFor(factoryType).getInstance();
    }

    public Class<?> getObjectType() {
        return targetType;
    }

    public boolean isSingleton() {
        return false;
    }

}
