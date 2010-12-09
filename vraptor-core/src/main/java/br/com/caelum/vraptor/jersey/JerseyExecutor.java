package br.com.caelum.vraptor.jersey;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Component
@RequestScoped
public class JerseyExecutor implements Interceptor{

	private final ExecuteMethodInterceptor delegate;
	private final Jersey jersey;
	private final HttpServletRequest request;

	public JerseyExecutor(MethodInfo info, Validator validator, Jersey jersey, HttpServletRequest request) {
		this.jersey = jersey;
		this.request = request;
		this.delegate = new ExecuteMethodInterceptor(info, validator);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) throws InterceptionException {
		if(jersey.isMine(request)) {
			jersey.execute(request, instance);
			stack.next(method, instance);
		} else {
			delegate.intercept(stack, method, instance);
		}
	}
}
