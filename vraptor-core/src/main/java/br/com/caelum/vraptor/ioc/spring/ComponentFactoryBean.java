package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentRegistrationException;
import br.com.caelum.vraptor.ioc.Container;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: Fabio Kung
 */
public class ComponentFactoryBean<T extends ComponentFactory> implements FactoryBean {

    private Container container;

    private Class<T> factoryType;
    private Class<?> targetType;

    public ComponentFactoryBean(Container container, Class<T> factoryType) {
        this.container = container;
        this.factoryType = factoryType;
        this.targetType = targetTypeForComponentFactory(factoryType);
    }

    public Object getObject() {
        return container.instanceFor(factoryType).getInstance();
    }

    public Class getObjectType() {
        return targetType;
    }

    public boolean isSingleton() {
        return false;
    }

    private Class targetTypeForComponentFactory(Class<?> type) {
        Type[] interfaces = type.getGenericInterfaces();
        for (Type implemented : interfaces) {
            if (implemented instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) implemented).getRawType();
                Type[] typeArguments = ((ParameterizedType) implemented).getActualTypeArguments();
                if (ComponentFactory.class.equals(rawType)) {
                    if (typeArguments == null || typeArguments.length < 1) {
                        throw new ComponentRegistrationException(
                                "The class implementing ComponentType<T> must define the generic argument. Eg.: " +
                                        "public class MyFactory implements ComponentFactory<MyComponent> { ... }");
                    }
                    return (Class) typeArguments[0];
                }
            }
        }

        throw new ComponentRegistrationException("Unable to determine target type, while trying to register " +
                "the ComponentFactory: " + type + ". The class implementing ComponentType<T> must define the " +
                "generic argument. Eg.: public class MyFactory implements ComponentFactory<MyComponent> { ... }");
    }


}
