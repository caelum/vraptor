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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A Controlled ParameterizedType
 *
 * @author Lucas Cavalcanti
 * @since 3.2
 *
 */
class HackedParameterizedType implements ParameterizedType {
	private final Class<?> target;
	private final Class<?> owner;

	HackedParameterizedType(Class<?> owner, Class<?> target) {
		this.owner = owner;
		this.target = target;
	}

	public Type getRawType() {
		return owner;
	}

	public Type getOwnerType() {
		return null;
	}

	public Type[] getActualTypeArguments() {
		return new Type[] {target};
	}
}