package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Marking interface to enable caching of the accepts method.
 * When implementing this interface your {@link Interceptor#accepts(br.com.caelum.vraptor.resource.ResourceMethod)} method
 * must only depends on given {@link ResourceMethod}.
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 *
 */
public interface StaticInterceptor extends Interceptor {

}
