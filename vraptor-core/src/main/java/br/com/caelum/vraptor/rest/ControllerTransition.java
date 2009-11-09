package br.com.caelum.vraptor.rest;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.Routes;


/**
 * A transition which will invoke a controller's method.
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 */
public class ControllerTransition implements Transition {

	private final Class<?> controller;
	private final String name;
	private final Routes routes;

	public ControllerTransition(Class<?> controller, String name, Routes routes) {
		this.controller = controller;
		this.name = name;
		this.routes = routes;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		Object object = routes.uriFor(controller);
		Method[] methods = object.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if(method.getName().equals(name)) {
				Object[] array = new Object[method.getParameterTypes().length];
				try {
					method.setAccessible(true);
					method.invoke(object, array);
				} catch (Exception e) {
					throw new IllegalStateException("Unable to retrieve uri for " + name + " from " + controller.getName(), e);
				}
			}
		}
		return routes.getApplicationPath() + routes.getUri();
	}

}
