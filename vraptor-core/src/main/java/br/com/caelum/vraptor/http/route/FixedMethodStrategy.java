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
import java.util.Set;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A route strategy which invokes a fixed type's method.
 *
 * @author guilherme silveira
 */
public class FixedMethodStrategy implements Route {

	private final ResourceMethod resourceMethod;

	private final Set<HttpMethod> methods;

	private final ParametersControl parameters;

	private final int priority;

	public FixedMethodStrategy(Class<?> type, Method method, Set<HttpMethod> methods, ParametersControl control, int priority) {
		this.methods = methods;
		this.parameters = control;
		this.resourceMethod = new DefaultResourceMethod(new DefaultResourceClass(type), method);
		this.priority = priority;
	}

	public boolean canHandle(Class<?> type, Method method) {
		return type.equals(this.resourceMethod.getResource().getType()) && method.equals(this.resourceMethod.getMethod());
	}

	public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
		boolean matches = canHandle(uri, method);
		if(matches) {
			parameters.fillIntoRequest(uri, request);
		}
		return matches ? this.resourceMethod : null;
	}

	public boolean canHandle(String uri, HttpMethod method) {
		boolean acceptMethod = this.methods.isEmpty() || this.methods.contains(method);
		boolean uriMatches = parameters.matches(uri);
		return uriMatches && acceptMethod;
	}

	public String urlFor(Class<?> type, Method m, Object params) {
		return parameters.fillUri(params);
	}

	public int getPriority() {
		return this.priority;
	}
}
