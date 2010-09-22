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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;

/**
 * Application Scope for guice.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public class ApplicationCustomScope implements LifecycleScope {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationCustomScope.class);

	private List<LifecycleListener> listeners;

	public void registerDestroyListener(LifecycleListener listener) {
		listeners.add(listener);
	}

	public void start() {
		listeners = new ArrayList<LifecycleListener>();
	}

	public void stop() {
		for (LifecycleListener listener : listeners) {
			try {
				listener.onEvent();
			} catch (Exception e) {
				logger.warn("Error while invoking PreDestroy", e);
			}
		}
	}

	public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
		return Scopes.SINGLETON.scope(key, provider);
	}

}
