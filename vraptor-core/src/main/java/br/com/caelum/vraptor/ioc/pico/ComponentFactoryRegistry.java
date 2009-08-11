package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ComponentFactory;

import java.util.Map;

public interface ComponentFactoryRegistry {
    public void register(Class<? extends ComponentFactory> componentFactoryClass);

    public Map<Class<?>, Class<? extends ComponentFactory>> getApplicationMap();

    public Map<Class<?>, Class<? extends ComponentFactory>> getSessionMap();

    public Map<Class<?>, Class<? extends ComponentFactory>> getRequestMap();
}