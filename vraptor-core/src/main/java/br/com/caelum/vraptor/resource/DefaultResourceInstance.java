package br.com.caelum.vraptor.resource;

public class DefaultResourceInstance {

	private final Object instance;
	private final ResourceMethod method;

	public DefaultResourceInstance(Object instance, ResourceMethod method) {
		this.instance = instance;
		this.method = method;
	}

}
