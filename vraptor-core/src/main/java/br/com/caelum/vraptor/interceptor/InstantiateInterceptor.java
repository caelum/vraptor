
package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Using a request scoped container, instantiates a resource.
 * 
 * @author Guilherme Silveira
 */
public class InstantiateInterceptor implements Interceptor {

    private final Container container;

    public InstantiateInterceptor(Container container) {
        this.container = container;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        Class<?> type = method.getResource().getType();
        Object instance = container.instanceFor(type);
        invocation.next(method, instance);
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
