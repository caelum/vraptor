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

import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.ioc.guice.RequestCustomScope.NullObject;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Guice's Session Scope. Based on GuiceWeb's session scope.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class SessionCustomScope implements LifecycleScope {

	private Multimap<String, LifecycleListener> listeners = LinkedListMultimap.create();

	public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
		final String name = key.toString();
		return new Provider<T>() {
			public T get() {
				HttpSession session = getSession();
				synchronized (session) {
					Object obj = session.getAttribute(name);
					if (NullObject.INSTANCE == obj) {
						return null;
					}
					@SuppressWarnings("unchecked")
					T t = (T) obj;
					if (t == null) {
						t = creator.get();
						session.setAttribute(name, (t != null) ? t : NullObject.INSTANCE);
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

	private HttpSession getSession() {
		return VRaptorRequestHolder.currentRequest().getRequest().getSession();
	}

	@Override
	public String toString() {
		return "SESSION";
	}

	public void registerDestroyListener(LifecycleListener listener) {
		listeners.put(getSession().getId(), listener);
	}

	public void start(HttpSession session) {
		listeners.removeAll(session.getId());
	}

	public void stop(HttpSession session) {
		for (LifecycleListener listener : listeners.removeAll(session.getId())) {
			listener.onEvent();
		}
	}
}
