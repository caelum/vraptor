package br.com.caelum.vraptor.interceptor.multipart;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A null implementation of {@link MultipartInterceptor}. This class doesn't anything.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.1.x-garcia
 * @see DefaultMultipartInterceptor
 */
public class NullMultipartInterceptor
    implements MultipartInterceptor {

    @Override
    public boolean accepts(ResourceMethod method) {
        return false; // never accepts
    }

    @Override
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        // go to next hope
        stack.next(method, resourceInstance);
    }

}
