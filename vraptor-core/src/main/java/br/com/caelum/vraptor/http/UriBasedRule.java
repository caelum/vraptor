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
package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class UriBasedRule implements Rule {
	private static final Logger logger = LoggerFactory.getLogger(UriBasedRule.class);

	private Method currentMethod;
	private Class definingType;

	private final String uri;
	private HttpMethod method;

	public UriBasedRule(String uri) {
		this.uri = uri;
	}

	public UriBasedRule with(HttpMethod method) {
		this.method = method;
		return this;
	}

	public <T> T invoke(Class<T> type) {
		Enhancer e = new Enhancer();
		e.setSuperclass(type);
		e.setCallback(new MethodInterceptor() {

			public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Class<? extends Object> baseType = instance.getClass();
				definingType = lookFor(baseType, baseType, method);
				currentMethod = method;
				return null;
			}

			private Class lookFor(Class<? extends Object> baseType, Class currentType, Method method) {
				if (currentType.equals(Object.class)) {
					throw new IllegalArgumentException("Invalid rule registration, method " + method.getName()
							+ " was not found, although it was declared at " + baseType.getName());
				}
				return null;
			}
		});
		return (T) e.create();
	}

	public boolean matches(String uri, HttpMethod method) {
		return false;
	}

	public ResourceMethod resourceMethod() {
		return null;
	}

}
