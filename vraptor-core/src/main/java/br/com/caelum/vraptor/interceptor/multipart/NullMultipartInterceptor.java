package br.com.caelum.vraptor.interceptor.multipart;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A null implementation of {@link MultipartInterceptor}. This class does nothing.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.3
 * @see CommonsUploadMultipartInterceptor
 */
@Lazy
public class NullMultipartInterceptor implements MultipartInterceptor {

    public boolean accepts(ResourceMethod method) {
        return false;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        stack.next(method, resourceInstance);
    }

}
