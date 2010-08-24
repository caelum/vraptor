package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.interceptor.Interceptor;

/**
 * Factory for InterceptorHandlers
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 *
 */
public interface InterceptorHandlerFactory {

	/**
	 * chooses the right handler for the given type
	 * @param type
	 * @return
	 */
	InterceptorHandler handlerFor(Class<? extends Interceptor> type);

}
