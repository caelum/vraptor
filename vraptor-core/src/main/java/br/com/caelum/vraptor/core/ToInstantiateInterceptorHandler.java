
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Instantiates the interceptor on the fly and executes its method.
 *
 * @author Guilherme Silveira
 */
public class ToInstantiateInterceptorHandler implements InterceptorHandler {

    private final Container container;
    private final Class<?> type;

    public ToInstantiateInterceptorHandler(Container container, Class<?> type) {
        this.container = container;
        this.type = type;
    }

    public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Interceptor interceptor = Interceptor.class.cast(container.instanceFor(type));
        if(interceptor==null) {
            throw new InterceptionException("Unable to instantiate interceptor for " + type.getName() + ": the container returned null.");
        }
        interceptor.intercept(stack, method, resourceInstance);
    }

}
