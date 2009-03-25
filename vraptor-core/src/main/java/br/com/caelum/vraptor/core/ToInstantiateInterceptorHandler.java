package br.com.caelum.vraptor.core;

import java.io.IOException;

import br.com.caelum.vraptor.Interceptor;
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
        if (Interceptor.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Type " + type.getName()
                    + " does not implement the interceptor interface");
        }
    }

    public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException {
        Interceptor interceptor = (Interceptor) container.instanceFor(type);
        interceptor.intercept(stack, method, resourceInstance);
    }

}
