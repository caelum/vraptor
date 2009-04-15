package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.Interceptor;

/**
 * Implements an interceptor stack capable of defining the order in which
 * interceptors are invoked.<br>
 * Interceptors within the set returned by getSequence are invoked in the
 * returned order.
 * 
 * @author Guilherme Silveira
 */
public interface InterceptorSequence {

    /**
     * Returns an array of interceptors to be invoked in such order.
     */
    Class<? extends Interceptor>[] getSequence();

}
