
package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Extracts all interceptors which are supposed to be applied for this current
 * resource method.
 *
 * @author Guilherme Silveira
 */
public class InterceptorListPriorToExecutionExtractor implements Interceptor {

    private final InterceptorRegistry registry;
    private final Container container;

    public InterceptorListPriorToExecutionExtractor(InterceptorRegistry registry, Container container) {
        this.registry = registry;
        this.container = container;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Interceptor[] interceptors = registry.interceptorsFor(method, container);
        for (int i = interceptors.length - 1; i >= 0; i--) {
			stack.addAsNext(interceptors[i]);
		}
        stack.next(method, resourceInstance);
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
