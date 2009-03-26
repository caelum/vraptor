package br.com.caelum.vraptor.resource;

import java.lang.annotation.Annotation;
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
			    Class<? extends Annotation> annotation = HttpMethod.valueOf(methodName).getAnnotation();
                if(method.isAnnotationPresent(annotation) || noAnnotationPresent(HttpMethod.values(), method)) {
			        return new DefaultResourceMethod(resource, method);
			    }
			}
		}
		return null;
	}

    private boolean noAnnotationPresent(HttpMethod[] values, Method method) {
        for (HttpMethod key : values) {
            if(method.isAnnotationPresent(key.getAnnotation())) {
                return false;
            }
        }
        return true;
    }

}
