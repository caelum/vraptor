/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.core;

import java.lang.reflect.Constructor;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

class LazyInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = LoggerFactory.getLogger(LazyInterceptorHandler.class);

	private final Container container;
	private final Class<? extends Interceptor> type;
	private Interceptor acceptor;

	public LazyInterceptorHandler(Container container, Class<? extends Interceptor> type) {
		this.container = container;
		this.type = type;
	}

	public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		boolean accepts;
		try {
			accepts = getAcceptor().accepts(method);
		} catch (NullPointerException e) {
			throw new InterceptionException("StaticInterceptors should not use constructor parameters inside the accepts method", e);
		}
		if (accepts) {
			Interceptor interceptor = container.instanceFor(type);
			if (interceptor == null) {
				throw new InterceptionException("Unable to instantiate interceptor for " + type.getName()
						+ ": the container returned null.");
			}
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			interceptor.intercept(stack, method, resourceInstance);
		} else {
			stack.next(method, resourceInstance);
		}

	}

	private Interceptor getAcceptor() {
		if (acceptor == null) {
			try {
				Constructor<?> constructor = type.getDeclaredConstructors()[0];
				int argsLength = constructor.getParameterTypes().length;
				acceptor = type.cast(new Mirror().on(type).invoke().constructor(constructor).withArgs(new Object[argsLength]));
			} catch (MirrorException e) {
				if (e.getCause() instanceof NullPointerException) {
					throw new InterceptionException("StaticInterceptors should not use constructor parameters inside the constructor", e);
				} else {
					throw new InterceptionException(e);
				}
			}
		}
		return acceptor;
	}

	@Override
	public String toString() {
		return "LazyInterceptorHandler for " + type.getName();
	}
}
