
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A route strategy which is basically invalid in order to force users to not
 * forget to decide a route strategy.
 *
 * @author guilherme silveira
 */
public class NoStrategy implements Route {

	public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
		throw new IllegalRouteException("You have created a route, but did not specify any method to be invoked.");
	}

	public String urlFor(Class<?> type, Method m, Object params) {
		return "nothing";
	}

	public boolean canHandle(Class<?> type, Method method) {
		return false;
	}

	public boolean canHandle(String uri, HttpMethod method) {
		return false;
	}

	public int getPriority() {
		return 0;
	}

}
