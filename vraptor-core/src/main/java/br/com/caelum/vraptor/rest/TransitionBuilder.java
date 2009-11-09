package br.com.caelum.vraptor.rest;

import br.com.caelum.vraptor.core.Routes;

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

	public TransitionBuilder(String name, Routes routes) {
		this.name = name;
		this.routes = routes;
	}

	public TransitionBuilder resultsInStatus(String newStatus) {
		this.resultingStatus = newStatus;
		return this;
	}

	public <T> T uses(Class<T> type) {
		this.controller = type;
		return null;
	}

	public TransitionBuilder uses(String customURI) {
		return this;
	}

	public Transition build() {
		if (controller != null) {
			return new ControllerTransition(controller, name, routes);
		}
		throw new IllegalStateException(
				"Transition was not correctly created: '" + name + "'");
	}

}
