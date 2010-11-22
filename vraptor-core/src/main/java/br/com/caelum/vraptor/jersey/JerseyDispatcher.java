/**
 * 
 */
package br.com.caelum.vraptor.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

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
		HttpServletRequest request = (HttpServletRequest) context.getProperties().get(DefaultJersey.REQUEST);
		request.setAttribute(FakeMethodDispatchProvider.METHOD_TO_EXECUTE, method);
		request.setAttribute(FakeMethodDispatchProvider.RESOURCE_TO_USE, resource);
		InterceptorStack stack = (InterceptorStack) request.getAttribute(DefaultJersey.INTERCEPTOR_STACK);
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