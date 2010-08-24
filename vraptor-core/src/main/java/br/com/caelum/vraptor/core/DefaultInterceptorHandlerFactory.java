package br.com.caelum.vraptor.core;

import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.StaticInterceptor;
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

	private Map<Class<? extends Interceptor>, InterceptorHandler> cachedHandlers = new HashMap<Class<? extends Interceptor>, InterceptorHandler>();

	public DefaultInterceptorHandlerFactory(Container container) {
		this.container = container;
	}


	public InterceptorHandler handlerFor(Class<? extends Interceptor> type) {
		if (StaticInterceptor.class.isAssignableFrom(type)) {
			if (!cachedHandlers.containsKey(type)) {
				cachedHandlers.put(type, new StaticInterceptorHandler(container, type));
			}
			return cachedHandlers.get(type);
		}
		return new ToInstantiateInterceptorHandler(container, type);
	}

}
