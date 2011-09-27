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

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * 
 * @author Lucas Cavalcanti
 * @since 3.3.0
 *
 */
public abstract class AbstractScope implements Scope {

	enum NullObject { INSTANCE }

	static interface ScopeHolder {
		Object getAttribute(String name);
		void setAttribute(String name, Object value);
	}

	private TypeNameExtractor extractor;

	@Inject
	public void setExtractor(TypeNameExtractor extractor) {
		this.extractor = extractor;
	}

	abstract ScopeHolder getHolder();
	abstract String getScopeName();

	public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
		return new ScopedProvider<T>(key, creator);
	}

	private class ScopedProvider<T> implements Provider<T> {
		private final Key<T> key;
		private final Provider<T> creator;
		private String name;

		private ScopedProvider(Key<T> key, Provider<T> creator) {
			this.key = key;
			this.creator = creator;
		}

		public T get() {
			ScopeHolder holder = getHolder();
			synchronized (holder) {
				Object obj = holder.getAttribute(getName());
				if (NullObject.INSTANCE == obj) {
					return null;
				}
				T t = (T) obj;
				if (t == null) {
					t = creator.get();
					holder.setAttribute(getName(), (t != null) ? t : NullObject.INSTANCE);
				}
				return t;
			}
		}

		@Override
		public String toString() {
			return String.format("%s[%s]", creator, getScopeName());
		}

		private String getName() {
			if (name == null) {
				name = extractor.nameFor(key.getTypeLiteral().getType());
			}
			return name;
		}
	}

}
