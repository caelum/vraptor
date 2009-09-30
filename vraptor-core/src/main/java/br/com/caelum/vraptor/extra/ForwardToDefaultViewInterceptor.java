
package br.com.caelum.vraptor.extra;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.Results;

/**
 * Intercepts the request and forwards to the default view if no view was
 * rendered so far.
 *
 * @author Guilherme Silveira
 */
public class ForwardToDefaultViewInterceptor implements Interceptor {
    private final Result result;
    private final MethodInfo methodInfo;

    public ForwardToDefaultViewInterceptor(Result result, MethodInfo methodResult) {
        this.result = result;
        this.methodInfo = methodResult;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        if (result.used()) {
            return;
        }
        result.use(Results.page()).forward();
    }

}
