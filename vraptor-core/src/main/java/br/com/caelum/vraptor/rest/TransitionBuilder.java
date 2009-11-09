package br.com.caelum.vraptor.rest;


/**
 * Builder to help creating transitions.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public class TransitionBuilder {

	private final DefaultStateControl defaultStateControl;
	private final String name;
	private String resultingStatus;
	private Class<?> controller;

	public TransitionBuilder(DefaultStateControl defaultStateControl,
			String name) {
		this.defaultStateControl = defaultStateControl;
		this.name = name;
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
		if(controller!=null) {
			return new ControllerTransition(controller, name);
		}
		throw new IllegalStateException("Transition was not correctly created: '" + name + "'");
	}

}
