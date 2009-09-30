
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Handles different rules in order to translate urls into Resource methods.
 *
 * @author Guilherme Silveira
 */
public interface Router {

	/**
	 * Add a new Route to this Router
	 * @param route The route
	 */
	void add(Route route);

	ResourceMethod parse(String uri, HttpMethod method, MutableRequest request);

	/**
	 * Registers this resource using the default algorithm for uri
	 * identification.
	 */
	void register(ResourceClass resource);

	/**
	 * Retrieves a single url to access the desired method.
	 */
	<T> String urlFor(Class<T> type, Method method, Object... params);

	/**
	 * Returns a list with all routes
	 */
	List<Route> allRoutes();

	RouteBuilder builderFor(String uri);
}
