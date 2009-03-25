package br.com.caelum.vraptor.core;

import java.io.IOException;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An interceptor handler is a wrapper to either an interceptor instance or a
 * interceptor definition. This way, the interceptor stack is capable of pushing
 * either already instantiated interceptors or
 * on-the-run-to-be-instantiated-interceptors. This gives an interceptor the
 * capability to require dependencies which will be provided by other
 * interceptors registered later on the interceptor stack (but in a previous position).
 * 
 * @author Guilherme Silveira
 */
public interface InterceptorHandler {

    void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException;

}
