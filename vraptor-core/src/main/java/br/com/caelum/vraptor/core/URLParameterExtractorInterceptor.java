
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class URLParameterExtractorInterceptor implements Interceptor {

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Path path = method.getResource().getType().getAnnotation(Path.class);
        if (path != null) {
        }
        stack.next(method, resourceInstance);
    }

}
