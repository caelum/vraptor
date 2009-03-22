package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;

public class DefaultResourceMethod implements ResourceMethod {

	private final Resource resource;
	private final Method method;

	public DefaultResourceMethod(Resource resource, Method method) {
		this.resource = resource;
		this.method = method;
	}
	
	public String toString() {
		return "{DefaultResourceMethod: " + method.getDeclaringClass().getName() + "." + method.getName() + "}";
	}

	public Method getMethod() {
		return method;
	}

}
