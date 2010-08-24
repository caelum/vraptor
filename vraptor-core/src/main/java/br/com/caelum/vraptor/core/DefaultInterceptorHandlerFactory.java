package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

/**
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 *
 */
@ApplicationScoped
public class DefaultInterceptorHandlerFactory implements InterceptorHandlerFactory {

	private Container container;

	public DefaultInterceptorHandlerFactory(Container container) {
		this.container = container;
	}


	public InterceptorHandler handlerFor(Class<? extends Interceptor> type) {
		return new ToInstantiateInterceptorHandler(container, type);
	}

}
