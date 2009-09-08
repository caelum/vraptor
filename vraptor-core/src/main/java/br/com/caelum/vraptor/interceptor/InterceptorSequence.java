package br.com.caelum.vraptor.interceptor;


/**
 * Implements an interceptor stack capable of defining the order in which
 * interceptors are invoked.<br>
 * Interceptors within the set returned by getSequence are invoked in the
 * returned order.
 *
 * You should not annotate returned interceptors neither with @Intercepts nor
 * @Component.
 * @author Guilherme Silveira
 */
public interface InterceptorSequence {

    /**
     * Returns an array of interceptors to be invoked in such order.
     */
    Class<? extends Interceptor>[] getSequence();

}
