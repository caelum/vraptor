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

import java.util.concurrent.ConcurrentMap;

import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.collect.MapMaker;

/**
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 *
 */
@ApplicationScoped
public class DefaultInterceptorHandlerFactory implements InterceptorHandlerFactory {

	private Container container;

	private ConcurrentMap<Class<? extends Interceptor>, InterceptorHandler> cachedHandlers =
		new MapMaker().makeMap();

	public DefaultInterceptorHandlerFactory(Container container) {
		this.container = container;
	}

	public InterceptorHandler handlerFor(Class<? extends Interceptor> type) {
		if (type.isAnnotationPresent(Lazy.class)) {
			InterceptorHandler handler = cachedHandlers.get(type);
			if (handler == null) {
				LazyInterceptorHandler value = new LazyInterceptorHandler(container, type);
				cachedHandlers.putIfAbsent(type, value);
				return value;
			} else {
				return handler;
			}
		}
		return new ToInstantiateInterceptorHandler(container, type);
	}

}
