/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.caelum.vraptor.http.ListOfRules;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.Rule;

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

    public Set<Resource> all() {
        return delegate.all();
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

    public void register(Resource resource) {
        delegate.register(resource);
    }

	public void add(ListOfRules rules) {
		delegate.add(rules);
	}

	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		return delegate.urlFor(type, method, params);
	}

	public List<Rule> allRoutes() {
		return delegate.allRoutes();
	}

}
