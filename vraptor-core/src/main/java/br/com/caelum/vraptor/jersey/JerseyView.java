package br.com.caelum.vraptor.jersey;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.extra.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Component
@RequestScoped
public class JerseyView implements Interceptor{
	
	private final ForwardToDefaultViewInterceptor delegate;
	private final Jersey jersey;
	private final HttpServletRequest req;

	public JerseyView(Result result, Jersey jersey, HttpServletRequest req) {
		this.jersey = jersey;
		this.req = req;
		this.delegate = new ForwardToDefaultViewInterceptor(result);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
		if(jersey.isMine(req)) {
			// oh boy, what now? do not worry, soon, soon
		} else {
			delegate.intercept(stack, method, resourceInstance);
		}
	}

}
