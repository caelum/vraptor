package br.com.caelum.vraptor.ioc.pico;

import java.io.IOException;

import br.com.caelum.vraptor.core.InstantiateInterceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResourceInstance;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * The interceptor responsible for instantiating the component and registering
 * it.
 * 
 * @author Guilherme Silveira
 */
public class PicoBasedInstantiateInterceptor implements InstantiateInterceptor {

    private final Container container;

    public PicoBasedInstantiateInterceptor(Container container) {
        this.container = container;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance) throws IOException {
        Class<?> type = method.getResource().getType();
        Object instance = container.instanceFor(type);
        container.register(new DefaultResourceInstance(instance, method));
        invocation.next(method, resourceInstance);
    }

}
