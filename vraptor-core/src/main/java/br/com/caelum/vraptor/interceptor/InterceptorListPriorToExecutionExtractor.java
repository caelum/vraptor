package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorListPriorToExecutionExtractor implements Interceptor{

    private final InterceptorRegistry registry;

    public InterceptorListPriorToExecutionExtractor(InterceptorRegistry registry) {
        this.registry = registry;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws IOException, InterceptionException {
        Class<? extends Interceptor>[] interceptors = registry.interceptorsFor(method);
        stack.addAsNext(interceptors);
        stack.next(method, resourceInstance);
    }

}
