package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.core.InstantiateInterceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.RequestContainer;
import br.com.caelum.vraptor.resource.DefaultResourceInstance;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class PicoBasedInstantiateInterceptor implements InstantiateInterceptor{
	
	private final ResourceMethod method;
	private final RequestContainer container;
	private final HttpServletRequest request;

	public PicoBasedInstantiateInterceptor(ResourceMethod method, RequestContainer container, HttpServletRequest request) {
		this.method = method;
		this.container = container;
		this.request = request;
	}

	public void intercept(InterceptorStack invocation) {
		Class<?> type = this.method.getResource().getType();
		container.register(type);
		Object instance = container.withA(type);
		container.register(new DefaultResourceInstance(instance, method));
		invocation.next();
	}

}
