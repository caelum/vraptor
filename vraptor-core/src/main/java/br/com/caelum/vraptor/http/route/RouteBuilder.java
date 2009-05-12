/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Should be used in one of two ways, either configure the type and invoke the
 * method or pass the method (java reflection) object.
 *
 * @author Guilherme Silveira
 */
public class RouteBuilder {
    private final Set<HttpMethod> supportedMethods = new HashSet<HttpMethod>();

    private final Proxifier proxifier;
	private final Logger logger = LoggerFactory.getLogger(RouteBuilder.class);

	private final String originalUri;
	
	private Route strategy = new NoStrategy();

	public RouteBuilder(Proxifier proxifier, String uri) {
        this.proxifier = proxifier;
		uri = uri.replaceAll("\\*", ".\\*");
		this.originalUri = uri;
	}

	public <T> T is(final Class<T> type) {
        return proxifier.proxify(type, new MethodInvocation<T>() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				boolean alreadySetTheStrategy = !strategy.getClass().equals(NoStrategy.class);
				if (alreadySetTheStrategy) {
					// the virtual machine might be invoking the finalize
                    return null;
                }
                is(type, method);
                return null;
            }
        });
    }

	public void is(PatternBasedType type, PatternBasedType method) {
		this.strategy = new PatternBasedStrategy(type, method, this.supportedMethods);
	}
/*
	public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
		if (!methodMatches(method)) {
			return null;
		}
		return uriMatches(uri, request);
	}

	private boolean methodMatches(HttpMethod method) {
		return (this.supportedMethods.isEmpty() || this.supportedMethods.contains(method));
	}

	@Override
    public String toString() {
        if (supportedMethods.isEmpty()) {
            return String.format("<< Route: %s => %s >>", originalUri, this.strategy.toString());
        }
        return String.format("<< Route: %s %s=> %s >>", originalUri, supportedMethods, this.strategy.toString());
    }

	private ResourceMethod uriMatches(String uri, MutableRequest request) {
		Matcher m = pattern.matcher(uri);
		if (!m.matches()) {
			return null;
		}
		for (int i = 1; i <= m.groupCount(); i++) {
			String name = parameters.get(i - 1);
			if(name.equals("_resource") || name.equals("_method")) {
				continue;
			}
			request.setParameter(name, m.group(i));
		}
		return this.strategy.getResourceMethod(m, request);
	}
*/
	public void is(Class<?> type, Method method) {
		this.strategy = new FixedMethodStrategy(originalUri, type, method, this.supportedMethods);
		logger.debug("created rule for path " + originalUri + " --> " + type.getName() + "." + method.getName());
	}

	/**
	 * Accepts also this http method request. If this method is not invoked, any
	 * http method is supported, otherwise all parameters passed are supported.
	 * 
	 * @param method
	 * @return
	 */
	public RouteBuilder with(HttpMethod method) {
		this.supportedMethods.add(method);
		return this;
	}

	public Route build() {
		return strategy;
	}

}
