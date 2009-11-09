package br.com.caelum.vraptor.core;

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
	 * Returns the application path, including the http protocol.<br>
	 * One can implement this method to return a fixed http/ip prefix.
	 */
	public String getApplicationPath();

}
