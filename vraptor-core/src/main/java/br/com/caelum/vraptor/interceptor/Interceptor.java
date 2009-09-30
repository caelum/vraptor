
package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Whenever an interceptor accepts a resource method, its intercept method is
 * invoked to intercept the process of request parsing in order to allow the
 * software to do some specific tasks.<br>
 * Common usage for interceptors for end-users (end-programmers) are security
 * constraint checks, database session (open session in view) opening and much
 * more.
 *
 * If you have an interceptor A which depends on an interceptor B, i.e, interceptor
 * B must be executed before interceptor A, use {@link InterceptorSequence}
 * @author Guilherme Silveira
 */
public interface Interceptor {

    void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException;

    boolean accepts(ResourceMethod method);

}
