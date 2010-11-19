package br.com.caelum.vraptor.jersey;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InstantiateComponentInterceptor implements Interceptor {

    private final Container container;
	private final Jersey jersey;

    public InstantiateComponentInterceptor(Container container, Jersey jersey) {
        this.container = container;
		this.jersey = jersey;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
//    		if(jersey.isMine(method)) {
//    			jersey.instantiate(method);
    	        invocation.next(method, null);
//    		} else {
//    			delegator.intercept(invocation, method, resource);
//    		}
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
