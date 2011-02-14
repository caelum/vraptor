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

package br.com.caelum.vraptor.util.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.AbstractResult;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.EmptyResult;
import br.com.caelum.vraptor.view.ResultException;

/**
 *
 * A mocked Result for testing your controllers.
 *
 * It will ignore redirections and accumulates included objects,
 * so you can use result.included() for inspect included objects.
 *
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 */
@Component
public class MockResult extends AbstractResult {

	private final Map<String, Object> values = new HashMap<String, Object>();
	protected Class<?> typeToUse;
	protected final Proxifier proxifier;
	
	public MockResult(Proxifier proxifier) {
		this.proxifier = proxifier;
	}

	public MockResult() {
	    this(new CglibProxifier(new ObjenesisInstanceCreator()));
	}

	public Result include(String key, Object value) {
		this.values.put(key, value);
		return this;
	}

	public Result on(Class<? extends Exception> exception) {
	    return this;
	}

	public <T extends View> T use(final Class<T> view) {
		this.typeToUse = view;
		if (view.equals(EmptyResult.class)) {
			return null;
		}
		return proxifier.proxify(view, returnOnFinalMethods(view));
	}

	protected <T> MethodInvocation<T> returnOnFinalMethods(final Class<T> view) {
		return new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				Class type = method.getReturnType();
				if (type == void.class) {
					return null;
				}

				if (view.isAssignableFrom(type)) {
					return proxy;
				}

				if (args.length > 0 && args[0] instanceof Class<?>) {
					return proxifier.proxify((Class<?>) args[0], returnOnFirstInvocation());
				}

				if (Serializer.class.isAssignableFrom(type)
						|| SerializerBuilder.class.isAssignableFrom(type)
						|| NoRootSerialization.class.isAssignableFrom(type)) {
					return proxifier.proxify(type, returnOnFinalMethods(type));
				}
				throw new ResultException("It's not possible to create a mocked version of " + method + ". Please inform this corner case to VRaptor developers");
			}

		};
	}

	private <T> MethodInvocation<T> returnOnFirstInvocation() {
		return new MethodInvocation<T>() {
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				return null;
			}
		};
	}

	public boolean used() {
		return typeToUse != null;
	}

	/**
	 *
	 * @param key
	 * @return the value if it was included
	 */
	@SuppressWarnings("unchecked")
	public <T> T included(String key) {
		return (T) values.get(key);
	}

	public Map<String, Object> included() {
		return values;
	}

	/**
	 * Uses the type name to include.
	 */
	public Result include(Object value) {
		return include(value.getClass().getSimpleName(), value);
	}

}
