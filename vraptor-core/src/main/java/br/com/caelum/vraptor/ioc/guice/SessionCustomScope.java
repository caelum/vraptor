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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Guice's Session Scope. Based on GuiceWeb's session scope.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class SessionCustomScope extends AbstractScope implements LifecycleScope {

	private static final Logger logger = LoggerFactory.getLogger(SessionCustomScope.class);

	private Multimap<String, LifecycleListener> listeners = LinkedListMultimap.create();

	private Provider<HttpSession> provider;

	@Inject
	public void setProvider(Provider<HttpSession> provider) {
		this.provider = provider;
	}

	@Override
	ScopeHolder getHolder() {
		return new ScopeHolder() {

			public void setAttribute(String name, Object value) {
				provider.get().setAttribute(name, value);
			}

			public Object getAttribute(String name) {
				return provider.get().getAttribute(name);
			}
		};
	}

	@Override
	String getScopeName() {
		return "SESSION";
	}

	public void registerDestroyListener(LifecycleListener listener) {
		listeners.put(provider.get().getId(), listener);
	}

	public void start(HttpSession session) {
		stop(session);
	}

	public void stop(HttpSession session) {
		for (LifecycleListener listener : listeners.removeAll(session.getId())) {
			stop(listener);
		}
	}

	public void stopAll() {
		for (LifecycleListener listener : listeners.values()) {
			stop(listener);
		}
	}

	private void stop(LifecycleListener listener) {
		try {
			listener.onEvent();
		} catch (Exception e) {
			logger.warn("Error while invoking PreDestroy", e);
		}
	}
}
