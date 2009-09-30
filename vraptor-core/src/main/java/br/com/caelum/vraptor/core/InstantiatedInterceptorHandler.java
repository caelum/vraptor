
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A handler based in an already instatiated interceptor. Whenever this handler
 * is invoked, the underlying interceptor is invoked.
 * 
 * @author Guilherme Silveira
 */
public class InstantiatedInterceptorHandler implements InterceptorHandler {

    private final Interceptor interceptor;

    public InstantiatedInterceptorHandler(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        interceptor.intercept(stack, method, resourceInstance);
    }

}
