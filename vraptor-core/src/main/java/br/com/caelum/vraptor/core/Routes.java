package br.com.caelum.vraptor.core;

import java.util.EnumSet;

import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Allows easy access to detect any route information.<br>
 * In order to access the uri for a method, one should invoke
 * <pre>routes.uriFor(OrderController.class).get(order); String uri = routes.getUri();</pre>
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Routes {

	/**
	 * Analyzes an uri for a specific controller.
	 * @param <T>	the controller
	 * @param type	the controller type
	 * @return
	 */
	public <T> T uriFor(final Class<T> type);

	/**
	 * Returns the last analyzed uri.
	 */
	public String getUri();

	/**
	 * Returns an enumset of allowed methods for this specific uri. 
	 */
	EnumSet<HttpMethod> allowedMethodsFor(String uri);

}
