package br.com.caelum.vraptor.jersey;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Either instantiates a jersey or a vraptor 3 component.
 * 
 * @author guilherme silveira
 */
@Component
@RequestScoped
public class InstantiateJerseyComponentInterceptor implements Interceptor {

	private final Jersey jersey;
	private final HttpServletRequest request;

	public InstantiateJerseyComponentInterceptor(Jersey jersey, HttpServletRequest request) {
		this.jersey = jersey;
		this.request = request;
	}

	public void intercept(InterceptorStack chain, ResourceMethod method,
			Object instance) throws InterceptionException {
		if (jersey.shouldInstantiate(method.getMethod().getDeclaringClass())) {
			instance = jersey.instantiate(request);
		}
		chain.next(method, instance);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

}
