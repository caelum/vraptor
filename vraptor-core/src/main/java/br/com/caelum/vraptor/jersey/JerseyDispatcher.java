/**
 * 
 */
package br.com.caelum.vraptor.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

final class JerseyDispatcher implements RequestDispatcher, ResourceMethod {
	private final AbstractResourceMethod method;

	JerseyDispatcher(AbstractResourceMethod method) {
		this.method = method;
	}

	public void dispatch(Object resource, HttpContext context) {
		context.getProperties().put(FakeMethodDispatchProvider.METHOD_TO_EXECUTE, method);
		context.getProperties().put(FakeMethodDispatchProvider.RESOURCE_TO_USE, resource);
		InterceptorStack stack = (InterceptorStack) context.getProperties().get(DefaultJersey.INTERCEPTOR_STACK);
		stack.next(this, resource);
	}

	public boolean containsAnnotation(Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}

	public Method getMethod() {
		return method.getMethod();
	}

	public ResourceClass getResource() {
		return new DefaultResourceClass(method.getDeclaringResource().getResourceClass());
	}
}