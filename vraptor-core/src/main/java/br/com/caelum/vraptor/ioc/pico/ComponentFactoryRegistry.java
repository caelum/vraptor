package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ComponentFactory;

import java.util.Map;

public interface ComponentFactoryRegistry {
    public void register(Class<? extends ComponentFactory> componentFactoryClass);

    public Map<Class<?>, Class<? extends ComponentFactory>> getApplicationScopedComponentFactoryMap();

    public Map<Class<?>, Class<? extends ComponentFactory>> getSessionScopedComponentFactoryMap();

    public Map<Class<?>, Class<? extends ComponentFactory>> getRequestScopedComponentFactoryMap();
}