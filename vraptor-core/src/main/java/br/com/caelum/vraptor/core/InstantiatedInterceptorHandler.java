package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.Interceptor;

/**
 * A handler based in an already instatiated interceptor. Whenever this handler
 * is invoked, the underlying interceptor is invoked.
 * 
 * @author Guilherme Silveira
 */
public class InstantiatedInterceptorHandler implements InterceptorHandler {

    private final Interceptor interceptor;

    public InstantiatedInterceptorHandler(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void execute(InterceptorStack stack) {
        interceptor.intercept(stack);
    }

}
