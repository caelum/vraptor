
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * The interceptors stack.
 * 
 * @author Guilherme Silveira
 */
public interface InterceptorStack {

    void add(Interceptor interceptor);

    <T extends Interceptor> void add(Class<T> interceptor);

    void next(ResourceMethod method, Object resourceInstance) throws InterceptionException;

    void addAsNext(Interceptor interceptor);

}
