
package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.RouteBuilder;
import br.com.caelum.vraptor.http.route.Router;

/**
 * A cached resource registry that avoids iterating over the entire set just in
 * order to find a resource already searched.
 *
 * @author Guilherme Silveira
 */
public class CacheBasedRouter implements Router {

    private final Router delegate;

    private final Map<String, Map<HttpMethod, ResourceMethod>> cache = new HashMap<String, Map<HttpMethod, ResourceMethod>>();

    public CacheBasedRouter(Router delegate) {
        this.delegate = delegate;
    }

    public ResourceMethod parse(String name, HttpMethod methodName, MutableRequest request) {
        if (!cache.containsKey(name)) {
            cache.put(name, new HashMap<HttpMethod, ResourceMethod>());
        }
        Map<HttpMethod, ResourceMethod> cachedMap = cache.get(name);
        if (!cachedMap.containsKey(methodName)) {
            cachedMap.put(methodName, delegate.parse(name, methodName, request));
        }
        return cachedMap.get(methodName);
    }

    public void register(ResourceClass resource) {
        delegate.register(resource);
    }

    public <T> String urlFor(Class<T> type, Method method, Object... params) {
        return delegate.urlFor(type, method, params);
    }

    public List<Route> allRoutes() {
        return delegate.allRoutes();
    }

    public RouteBuilder builderFor(String uri) {
    	return delegate.builderFor(uri);
    }
	public void add(Route route) {
		delegate.add(route);
	}

}
