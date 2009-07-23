package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Intercepts()
public class DummyInterceptor implements Interceptor {

	@Override
	public boolean accepts(ResourceMethod method) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		// TODO Auto-generated method stub
		
	}

}
