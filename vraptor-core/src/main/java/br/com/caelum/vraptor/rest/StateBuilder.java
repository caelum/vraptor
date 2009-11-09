package br.com.caelum.vraptor.rest;

public class StateBuilder {

	private final String name;

	public StateBuilder( String name) {
		this.name = name;
	}

	public StateBuilder allow(String ... states) {
		return this;
	}
	
	public StateBuilder when(Dependency dependency) {
		return this;
	}

	public State build() {
		return null;
	}

}
