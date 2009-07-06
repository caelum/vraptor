package br.com.caelum.vraptor.ioc;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

/**
 * @author Fabio Kung
 */
public class ComponentFactoryIntrospector {
    public Class<?> targetTypeForComponentFactory(Class<?> type) {
        Type[] interfaces = type.getGenericInterfaces();
        for (Type implemented : interfaces) {
            if (implemented instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) implemented).getRawType();
                Type[] typeArguments = ((ParameterizedType) implemented).getActualTypeArguments();
                if (ComponentFactory.class.equals(rawType)) {
                    if (typeArguments == null || typeArguments.length < 1) {
                        throw new ComponentRegistrationException(
                                "The class implementing ComponentFactory<T> must define the generic argument. Eg.: " +
                                        "public class MyFactory implements ComponentFactory<MyComponent> { ... }");
                    }
                    return (Class) typeArguments[0];
                }
            }
        }

        throw new ComponentRegistrationException("Unable to determine target type, while trying to register " +
                "the ComponentFactory: " + type + ". The class implementing ComponentFactory<T> must define the " +
                "generic argument. Eg.: public class MyFactory implements ComponentFactory<MyComponent> { ... }");
    }

}
