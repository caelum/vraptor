
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An specific route rule.
 *
 * @author Guilherme Silveira
 */
public interface Route {

	/**
	 * Returns the resource method for this specifig rule. Also applies the
	 * required parameters to this vraptor request.
	 */
	ResourceMethod matches(String uri, HttpMethod method, MutableRequest request);

	/**
	 * Returns if this route can handle this request
	 */
	boolean canHandle(String uri, HttpMethod method);

	/**
	 * Returns the url which invokes this rule with values extracted from this
	 * parameter object. The object contains getters representing each method's
	 * parameter.
	 */
	String urlFor(Class<?> type, Method m, Object params);

	/**
	 * Returns true if this route is able to redirect to this method.
	 */
	boolean canHandle(Class<?> type, Method method);

	/**
	 * Routes will be ordered according to priority.
	 * Routes with lower values of priority will be tested first.
	 * @return route priority
	 */
	int getPriority();
}
