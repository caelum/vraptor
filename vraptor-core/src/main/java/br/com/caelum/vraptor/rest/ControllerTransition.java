package br.com.caelum.vraptor.rest;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.Routes;

/**
 * A transition which will invoke a controller's method.
 * 
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 */
public class ControllerTransition implements Transition {

	private final Class<?> controller;
	private final String name;
	private final Routes routes;
	private final Method method;
	private final Object[] parameters;

	public ControllerTransition(Class<?> controller, String rel, Method method,
			Object[] parameters, Routes routes) {
		this.controller = controller;
		this.name = rel;
		this.method = method;
		this.parameters = parameters;
		this.routes = routes;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		Object object = routes.uriFor(controller);
		method.setAccessible(true);
		try {
			method.invoke(object, parameters);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to retrieve uri for "
					+ name + " from " + controller.getName(), e);
		}
		return routes.getUri();
	}

}
