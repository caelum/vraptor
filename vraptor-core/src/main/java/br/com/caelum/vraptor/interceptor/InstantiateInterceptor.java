package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InstantiateInterceptor implements Interceptor {

    private final Container container;

    public InstantiateInterceptor(Container container) {
        this.container = container;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException {
        Class<?> type = method.getResource().getType();
        Object instance = container.instanceFor(type);
        invocation.next(method, instance);
    }

}
