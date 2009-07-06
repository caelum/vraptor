package br.com.caelum.vraptor.ioc.pico;

import java.util.Map;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public interface ComponentFactoryRegistry {
	public void register(Class<? extends ComponentFactory<?>> componentFactoryClass);
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getApplicationScopedComponentFactoryMap();
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getSessionScopedComponentFactoryMap();
	public Map<Class<?>, Class<? extends ComponentFactory<?>>> getRequestScopedComponentFactoryMap();
}