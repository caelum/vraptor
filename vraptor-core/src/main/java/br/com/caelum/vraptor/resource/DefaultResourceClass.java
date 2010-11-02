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

package br.com.caelum.vraptor.resource;

public class DefaultResourceClass implements ResourceClass {

	private final Class<?> type;

	public DefaultResourceClass(Class<?> type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultResourceClass)) {
			return false;
		}
		if (obj == null) {
			return false;
		}
		DefaultResourceClass resource = (DefaultResourceClass) obj;
		if (this.type == null && resource.type != null) {
			return false;
		}
		return this.type.equals(resource.type);
	}

	@Override
	public int hashCode() {
		return type == null ? 0 : type.hashCode();
	}

	@Override
	public String toString() {
		return "{ResourceClass " + type.getName() + "}";
	}

	public Class<?> getType() {
		return type;
	}

}
