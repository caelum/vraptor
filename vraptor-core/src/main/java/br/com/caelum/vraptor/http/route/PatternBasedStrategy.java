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
import java.lang.reflect.Modifier;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A strategy which returns the resource method based on a url pattern.
 *
 * @author guilherme silveira
 */
public class PatternBasedStrategy implements Route {

	private final Logger logger = LoggerFactory.getLogger(PatternBasedStrategy.class);

	private final PatternBasedType type;
	private final PatternBasedType method;
	private final Set<HttpMethod> methods;

	private final ParametersControl control;

	private final int priority;

	public PatternBasedStrategy(ParametersControl control, PatternBasedType type, PatternBasedType method, Set<HttpMethod> methods, int priority) {
		this.control = control;
		this.type = type;
		this.method = method;
		this.methods = methods;
		this.priority = priority;
	}

	public boolean canHandle(Class<?> type, Method method) {
		return this.type.matches(type.getName()) && this.method.matches(method.getName());
	}

	public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
		boolean acceptMethod = this.methods.isEmpty() || this.methods.contains(method);
		boolean acceptUri = control.matches(uri);
		if(acceptUri && acceptMethod){
			control.fillIntoRequest(uri, request);
			String webLogic = request.getParameter("webLogic");
			String webMethod = request.getParameter("webMethod");
			String typeName = type.apply("webLogic",webLogic);
			try {
				DefaultResourceClass resource = new DefaultResourceClass(Class.forName(typeName));
				Method resourceMethod = method(resource.getType(), this.method.apply("webMethod", webMethod));
				return new DefaultResourceMethod(resource, resourceMethod);
			} catch (ClassNotFoundException e) {
				logger.debug("Unable to find type " + typeName + " for strategy " + this);
				return null;
			}
		}
		return null;
	}

	private Method method(Class<?> type, String methodName) {
		Method[] methods = type.getDeclaredMethods();
		for(Method m :methods) {
			if(m.getName().equals(methodName) && isEligible(m)) {
				return m;
			}
		}
		if(type.getSuperclass().equals(Object.class)) {
			return null;
		}
		return method(type.getSuperclass(), methodName);
	}

	private boolean isEligible(Method m) {
		return Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers());
	}

	public String urlFor(Class<?> type, Method m, Object params) {
		return control.apply(new String[] {this.type.extract("webLogic", type.getName()), this.method.extract("webMethod", m.getName())});
	}

	public ResourceClass getResource() {
		return null;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
