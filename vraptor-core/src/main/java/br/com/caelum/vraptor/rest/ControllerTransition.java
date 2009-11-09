package br.com.caelum.vraptor.rest;


/**
 * A transition which will invoke a controller's method.
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 */
public class ControllerTransition implements Transition {

	private final Class<?> controller;
	private final String name;

	public ControllerTransition(Class<?> controller, String name) {
		this.controller = controller;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return null;
	}

}
