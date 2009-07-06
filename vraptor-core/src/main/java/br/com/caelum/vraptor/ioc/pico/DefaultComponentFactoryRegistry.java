package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.SessionScoped;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry to all ComponentRegistry classes
 *
 * @author Sérgio Lopes
 */
@ApplicationScoped
public class DefaultComponentFactoryRegistry implements ComponentFactoryRegistry {

    /* maps from targetClass to componentFactoryClass */
    private final Map<Class<?>, Class<? extends ComponentFactory<?>>> applicationScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();
    private final Map<Class<?>, Class<? extends ComponentFactory<?>>> sessionScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();
    private final Map<Class<?>, Class<? extends ComponentFactory<?>>> requestScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory<?>>>();

    private ComponentFactoryIntrospector componentFactoryIntrospector = new ComponentFactoryIntrospector();

    public void register(Class<? extends ComponentFactory<?>> componentFactoryClass) {
        Class<?> targetType = componentFactoryIntrospector.targetTypeForComponentFactory(componentFactoryClass);

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
}
