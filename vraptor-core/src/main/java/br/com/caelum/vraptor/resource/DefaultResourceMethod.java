
package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;

public class DefaultResourceMethod implements ResourceMethod {

	private final ResourceClass resource;
	private final Method method;

	public DefaultResourceMethod(ResourceClass resource, Method method) {
		this.resource = resource;
		this.method = method;
	}

	public static ResourceMethod instanceFor(Class<?> type, Method method) {
		return new DefaultResourceMethod(new DefaultResourceClass(type), method);
	}

	@Override
	public String toString() {
		return "{DefaultResourceMethod: " + method.getDeclaringClass().getName() + "." + method.getName() + "}";
	}

	public Method getMethod() {
		return method;
	}

	public ResourceClass getResource() {
		return resource;
	}

}
