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
import com.sun.jersey.server.impl.application.DispatcherFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

final class JerseyDispatcher implements RequestDispatcher, ResourceMethod {
	private final AbstractResourceMethod method;

	public static final String METHOD_TO_EXECUTE = FakeMethodDispatchProvider.class.getPackage() + ".method";
	public static final String RESOURCE_TO_USE = FakeMethodDispatchProvider.class.getPackage() + ".resource";
	public static final String DISPATCHER = FakeMethodDispatchProvider.class.getPackage() + ".dispatcher";
	public static final String HTTP_CONTEXT = FakeMethodDispatchProvider.class.getPackage() + ".http_context";

	private final DispatcherFactory delegate;

	JerseyDispatcher(DispatcherFactory delegate, AbstractResourceMethod method) {
		this.delegate = delegate;
		this.method = method;
	}

	public void dispatch(Object resource, HttpContext context) {
		HttpServletRequest request = (HttpServletRequest) context.getProperties().get(DefaultJersey.REQUEST);
		request.setAttribute(METHOD_TO_EXECUTE, method);
		request.setAttribute(RESOURCE_TO_USE, resource);
		request.setAttribute(DISPATCHER, this);
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

	public void execute(Object resource, HttpServletRequest request) {
		HttpContext context = (HttpContext) request.getAttribute(HTTP_CONTEXT);
		delegate.getDispatcher(method).dispatch(resource, context);
	}
}