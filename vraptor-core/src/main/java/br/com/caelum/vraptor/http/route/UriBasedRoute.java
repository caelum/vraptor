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

import br.com.caelum.vraptor.eval.Evaluator;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Should be used in one of two ways, either configure the type and invoke the
 * method or pass the method (java reflection) object.
 *
 * @author Guilherme Silveira
 */
public class UriBasedRoute implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(UriBasedRoute.class);

    private final Proxifier proxifier;

    private DefaultResourceMethod resourceMethod;

    private final Set<HttpMethod> supportedMethods = new HashSet<HttpMethod>();

    private final Pattern pattern;

    private final List<String> parameters = new ArrayList<String>();

    private final String patternUri;
    private final String originalUri;

    public UriBasedRoute(Proxifier proxifier, String uri) {
        this.proxifier = proxifier;
        this.originalUri = uri;
        uri = uri.replaceAll("\\*", ".\\*");
        String patternUri = "";
        String paramName = "";
        // not using stringbuffer because this is only run in startup
        boolean ignore = false;
        for (int i = 0; i < uri.length(); i++) {
            if (uri.charAt(i) == '{') {
                ignore = true;
                patternUri += "(";
            } else if (uri.charAt(i) == '}') {
                ignore = false;
                patternUri += ".*)";
                parameters.add(paramName);
                paramName = "";
            } else if (!ignore) {
                patternUri += uri.charAt(i);
            } else {
                paramName += uri.charAt(i);
            }
        }
        this.patternUri = patternUri;
        this.pattern = Pattern.compile(patternUri);
    }

    /**
     * Accepts also this http method request. If this method is not invoked, any
     * http method is supported, otherwise all parameters passed are supported.
     *
     * @param method to be supported
     * @return this
     */
    public UriBasedRoute with(HttpMethod method) {
        this.supportedMethods.add(method);
        return this;
    }

    public <T> T is(final Class<T> type) {
        return proxifier.proxify(type, new MethodInvocation<T>() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                if (resourceMethod != null) {
                    // you are either invoking a second method
                    // or the virtual machine might be invoking the finalize
                    // method (or anything similar)
                    // therefore we should ignore this emthod invocation
                    return null;
                }
                is(type, method);
                return null;
            }
        });
    }

    public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
        if (methodMatches(method)) {
            return uriMatches(uri, request);
        }
        return null;
    }

    private DefaultResourceMethod uriMatches(String uri, MutableRequest request) {
        Matcher m = pattern.matcher(uri);
        if (!m.matches()) {
            return null;
        }
        for (int i = 1; i <= m.groupCount(); i++) {
            request.setParameter(parameters.get(i - 1), m.group(i));
        }
        return resourceMethod;
    }

    private boolean methodMatches(HttpMethod method) {
        return (this.supportedMethods.isEmpty() || this.supportedMethods.contains(method));
    }

    public Resource getResource() {
        if (resourceMethod == null) {
            throw new IllegalStateException(
                    "You forgot to invoke a method to let the rule know which method it is suposed to invoke.");
        }
        return this.resourceMethod.getResource();
    }

    public void is(Class<?> type, Method method) {
        logger.debug("created rule for path " + patternUri + " --> " + type.getName() + "." + method.getName());
        resourceMethod = new DefaultResourceMethod(new DefaultResource(type), method);
    }

    public ResourceMethod getResourceMethod() {
        return resourceMethod;
    }

    public String urlFor(Object params) {
        String base = originalUri.replaceAll("\\.\\*", "");
        for (String key : parameters) {
            Object result = new Evaluator().get(params, key);
            base = base.replace("{" + key + "}", result == null ? "" : result.toString());
        }
        return base;
    }

    public void is(PatternBasedType type, PatternBasedType method) {
        // TODO implement! it was empty
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (supportedMethods.isEmpty()) {
            return String.format("<< Route: %s => %s >>", originalUri, resourceMethod);
        }
        return String.format("<< Route: %s %s=> %s >>", originalUri, supportedMethods, resourceMethod);
    }
}
