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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class UriBasedRule implements Rule {
	private static final Logger logger = LoggerFactory.getLogger(UriBasedRule.class);

	private DefaultResourceMethod resource;

	private HttpMethod method;

	private Pattern pattern;
	
	private final List<String> parameters = new ArrayList<String>();

	public UriBasedRule(String uri) {
		uri = uri.replaceAll("\\*", ".\\*");
		String finalUri = "";
		String patternUri = "";
		String paramName = "";
		// not using stringbuffer because this is only run in startup
		boolean ignore = false;
		for (int i = 0; i < uri.length(); i++) {
			if (uri.charAt(i) == '{') {
				ignore = true;
				patternUri += "(";
				continue;
			} else if (uri.charAt(i) == '}') {
				ignore = false;
				finalUri += ".*";
				patternUri += ".*)";
				parameters.add(paramName);
				paramName = "";
				continue;
			} else if (!ignore) {
				patternUri += uri.charAt(i);
				finalUri += uri.charAt(i);
			} else {
				paramName += uri.charAt(i);
			}
		}
		this.pattern = Pattern.compile(patternUri);
	}

	public UriBasedRule with(HttpMethod method) {
		this.method = method;
		return this;
	}

	public <T> T is(Class<T> type) {
		Enhancer e = new Enhancer();
		e.setSuperclass(type);
		e.setCallback(new MethodInterceptor() {

			public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Class<? extends Object> baseType = instance.getClass();
				Class definingType = lookFor(baseType, baseType, method);
				if(resource!=null) {
					throw new IllegalStateException("You invoked two methods to define what a rule is supposed to do. That's invalid.");
				}
				resource = new DefaultResourceMethod(new DefaultResource(definingType), method);
				return null;
			}

			private Class lookFor(Class<? extends Object> baseType, Class currentType, Method method) {
				if (currentType.equals(Object.class)) {
					throw new IllegalArgumentException("Invalid rule registration, method " + method.getName()
							+ " was not found, although it was declared at " + baseType.getName());
				}
				try {
					currentType.getDeclaredMethod(method.getName(), method.getParameterTypes());
					return currentType;
				} catch (SecurityException e) {
					throw new InvalidResourceException("We were unable to reflection access type " + currentType.getName() + " while adding a " + baseType.getName() + "." + method.getName());
				} catch (NoSuchMethodException e) {
					return lookFor(baseType, currentType.getSuperclass(), method);
				}
			}
		});
		return (T) e.create();
	}

	public ResourceMethod matches(String uri, HttpMethod method, MutableRequest request) {
		if(!methodMatches(method)) {
			return null;
		}
		return uriMatches(uri, request);
	}

	private DefaultResourceMethod uriMatches(String uri, MutableRequest request) {
		Matcher m = pattern.matcher(uri);
		if(!m.matches()) {
			return null;
		}
		for(int i=1;i<=m.groupCount();i++) {
			request.setParameter(parameters.get(i-1), m.group(i));
		}
		return resource;
	}

	private boolean methodMatches(HttpMethod method) {
		return (this.method == null || this.method.equals(method));
	}

	public Resource getResource() {
		if(resource==null) {
			throw new IllegalStateException("You forgot to invoke a method to let the rule know which method it is suposed to invoke.");
		}
		return this.resource.getResource();
	}

}
