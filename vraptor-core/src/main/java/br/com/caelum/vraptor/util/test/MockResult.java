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

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.serialization.Serializer;
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
public class MockResult implements Result {

	private final Map<String, Object> values = new HashMap<String, Object>();
	private Class<?> typeToUse;
	private final Proxifier proxifier;

	public MockResult(Proxifier proxifier) {
		this.proxifier = proxifier;
	}

	public MockResult() {
		this(new ObjenesisProxifier());
	}

	public Result include(String key, Object value) {
		this.values.put(key, value);
		return this;
	}

	public <T extends View> T use(final Class<T> view) {
		this.typeToUse = view;
		if (view.equals(EmptyResult.class)) {
			return null;
		}
		return proxifier.proxify(view, returnOnFinalMethods(view));
	}

	private <T> MethodInvocation<T> returnOnFinalMethods(final Class<T> view) {
		return new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				if (method.getReturnType() == void.class) {
					return null;
				}

				if (view.isAssignableFrom(method.getReturnType())) {
					return proxy;
				}

				if (args.length > 0 && args[0] instanceof Class<?>) {
					return proxifier.proxify((Class<?>) args[0], returnOnFirstInvocation());
				}

				if (Serializer.class.isAssignableFrom(method.getReturnType())) {
					return proxifier.proxify(Serializer.class, returnOnFinalMethods(Serializer.class));
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

	public void forwardTo(String uri) {
		use(page()).forward(uri);
	}

	public <T> T forwardTo(Class<T> controller) {
		return use(logic()).forwardTo(controller);
	}

	public <T> T redirectTo(Class<T> controller) {
		return use(logic()).redirectTo(controller);
	}

	public <T> T of(Class<T> controller) {
		return use(page()).of(controller);
	}

	@SuppressWarnings("unchecked")
	public <T> T redirectTo(T controller) {
		return (T) redirectTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T forwardTo(T controller) {
		return (T) forwardTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T of(T controller) {
		return (T) of(controller.getClass());
	}
}
