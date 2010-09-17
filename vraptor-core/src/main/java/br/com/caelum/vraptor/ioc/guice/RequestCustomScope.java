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
package br.com.caelum.vraptor.ioc.guice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Guice's Request Scope. Based on GuiceWeb request scope.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class RequestCustomScope implements LifecycleScope {

	enum NullObject { INSTANCE }

	private static final Logger logger = LoggerFactory.getLogger(RequestCustomScope.class);
	private final ThreadLocal<List<LifecycleListener>> listeners = new ThreadLocal<List<LifecycleListener>>();


	public void start() {
		listeners.set(new ArrayList<LifecycleListener>());
	}

	public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
		final String name = key.toString();
		return new Provider<T>() {
			public T get() {
				HttpServletRequest request = VRaptorRequestHolder.currentRequest().getRequest();
				synchronized (request) {
					Object obj = request.getAttribute(name);
					if (NullObject.INSTANCE == obj) {
						return null;
					}
					@SuppressWarnings("unchecked")
					T t = (T) obj;
					if (t == null) {
						t = creator.get();
						request.setAttribute(name, (t != null) ? t : NullObject.INSTANCE);
					}
					return t;
				}
			}

			@Override
			public String toString() {
				return String.format("%s[%s]", creator, this);
			}
		};
	}

	public void stop() {
		for (LifecycleListener listener : listeners.get()) {
			try {
				listener.onEvent();
			} catch (Exception e) {
				logger.warn("Error while invoking PreDestroy", e);
			}
		}
	}

	@Override
	public String toString() {
		return "REQUEST";
	}

	public void registerDestroyListener(LifecycleListener listener) {
		listeners.get().add(listener);
	}
}
