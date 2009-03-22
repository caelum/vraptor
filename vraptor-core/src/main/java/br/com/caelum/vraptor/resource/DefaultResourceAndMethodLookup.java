package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.Path;

/**
 * The default resource method lookup algorithm. It goes through every method
 * searching for one mapped with the same id in the Path annotation.
 * 
 * @author Guilherme Silveira
 */
public class DefaultResourceAndMethodLookup {

	private final Resource resource;

	public DefaultResourceAndMethodLookup(Resource resource) {
		this.resource = resource;
	}

	public ResourceMethod methodFor(String id, String methodName) {
		for (Method method : resource.getType().getDeclaredMethods()) {
			if (!method.isAnnotationPresent(Path.class)) {
				continue;
			}
			Path path = method.getAnnotation(Path.class);
			if (path.value().equals(id)) {
				return new DefaultResourceMethod(resource, method);
			}
		}
		return null;
	}

}
