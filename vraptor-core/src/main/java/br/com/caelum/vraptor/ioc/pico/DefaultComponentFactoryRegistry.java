package br.com.caelum.vraptor.ioc.pico;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentRegistrationException;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * Registry to all ComponentRegistry classes
 * 
 * @author Sérgio Lopes
 */
@ApplicationScoped
public class DefaultComponentFactoryRegistry implements ComponentFactoryRegistry {
	
	/* maps from targetClass to componentFactoryClass */
	private final Map<Class<?>, Class<? extends ComponentFactory<?>>> applicationScoped = new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();
	private final Map<Class<?>, Class<? extends ComponentFactory<?>>> sessionScoped = new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();
	private final Map<Class<?>, Class<? extends ComponentFactory<?>>> requestScoped = new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();
	
	public void register(Class<? extends ComponentFactory<?>> componentFactoryClass) {
		Class<?> targetType = targetTypeForComponentFactory(componentFactoryClass);
		
		if (componentFactoryClass.isAnnotationPresent(ApplicationScoped.class)) {
			applicationScoped.put(targetType, componentFactoryClass);
		} else if (componentFactoryClass.isAnnotationPresent(SessionScoped.class)) {
			sessionScoped.put(targetType, componentFactoryClass);
		} else { // @RequestScoped
			requestScoped.put(targetType, componentFactoryClass);
		}
	}
	
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getApplicationScopedComponentFactoryMap() {
		return applicationScoped;
	}
	
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getSessionScopedComponentFactoryMap() {
		return sessionScoped;
	}
	
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getRequestScopedComponentFactoryMap() {
		return requestScoped;
	}
	
    private Class<?> targetTypeForComponentFactory(Class<?> type) {
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
                    return (Class<?>) typeArguments[0];
                }
            }
        }

        throw new ComponentRegistrationException("Unable to determine target type, while trying to register " +
                "the ComponentFactory: " + type + ". The class implementing ComponentFactory<T> must define the " +
                "generic argument. Eg.: public class MyFactory implements ComponentFactory<MyComponent> { ... }");
    }

}
