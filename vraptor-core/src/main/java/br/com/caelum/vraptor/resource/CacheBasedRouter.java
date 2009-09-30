/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

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
