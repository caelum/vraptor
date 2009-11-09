package br.com.caelum.vraptor.rest;

import java.lang.reflect.Method;

import net.vidageek.mirror.Mirror;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;

/**
 * Builder to help creating transitions.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public class TransitionBuilder {

	private final String name;
	private String resultingStatus;
	private Class<?> controller;
	private final Routes routes;
	private final Proxifier proxifier;
	private Method method;
	private Object[] parameters = new Object[0];

	public TransitionBuilder(String name, Routes routes, Proxifier proxifier) {
		this.name = name;
		this.routes = routes;
		this.proxifier = proxifier;
	}

	public TransitionBuilder resultsInStatus(String newStatus) {
		this.resultingStatus = newStatus;
		return this;
	}

	public <T> T uses(Class<T> type) {
		this.controller = type;
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, java.lang.reflect.Method method,
					Object[] args, br.com.caelum.vraptor.proxy.SuperMethod superMethod) {
				TransitionBuilder.this.method = method;
				parameters = args;
				return null;
			};
		});
	}

	public TransitionBuilder uses(String customURI) {
		return this;
	}

	public Transition build() {
		if (controller != null) {
			if(method==null) {
				method = findMethod(name, controller);
				parameters = new Object[method.getParameterTypes().length];
			}
			return new ControllerTransition(controller, name, method, parameters, routes);
		}
		throw new IllegalStateException(
				"Transition was not correctly created: '" + name + "'");
	}

	private Method findMethod(String name, Class type) {
		if(type.equals(Object.class)) {
			throw new IllegalArgumentException("Controller " + controller.getName() + " does not have a method named " + name);
		}
		for(Method m : type.getDeclaredMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return findMethod(name, type.getSuperclass());
	}

}
