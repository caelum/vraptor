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

package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Outjects the result of the method invocation to the desired result
 *
 * @author guilherme silveira
 */
public class OutjectResult implements Interceptor {

	private final Result result;
	private final MethodInfo info;

	public OutjectResult(Result result, MethodInfo info) {
		this.result = result;
		this.info = info;
	}

	public boolean accepts(ResourceMethod method) {
		return method.getResource().getType().isAnnotationPresent(Resource.class);
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		Type returnType = method.getMethod().getGenericReturnType();
		if (!returnType.equals(void.class)) {
			result.include(nameFor(returnType), this.info.getResult());
		}
		stack.next(method, resourceInstance);
	}

	/*
	 * TODO: externalize as an application Component
	 * and maybe use a cache.
	 */
	@SuppressWarnings("unchecked")
	String nameFor(Type generic) {
		if (generic instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) generic;
			Class raw = (Class) type.getRawType();
			if (Collection.class.isAssignableFrom(raw)) {
				return nameFor(type.getActualTypeArguments()[0]) + "List";
			}
			return nameFor(raw);
		}

		if (generic instanceof WildcardType) {
			WildcardType wild = (WildcardType) generic;
			if ((wild.getLowerBounds().length != 0)) {
				return nameFor(wild.getLowerBounds()[0]);
			}
			else return nameFor(wild.getUpperBounds()[0]);
		}

		Class raw = (Class) generic;


		if (raw.isArray()) {
			return nameFor(raw.getComponentType()) + "List";
		}

		String name = raw.getSimpleName();

		// common case: SomeClass -> someClass
		if(Character.isLowerCase(name.charAt(1))) {
			return Info.decapitalize(name);
		}

		// different case: URLClassLoader -> urlClassLoader
		for (int i = 1; i < name.length(); i++) {
			if(Character.isLowerCase(name.charAt(i))) {
				return name.substring(0, i-1).toLowerCase()+name.substring(i-1, name.length());
			}
		}

		// all uppercase: URL -> url
		return name.toLowerCase();
	}

}
