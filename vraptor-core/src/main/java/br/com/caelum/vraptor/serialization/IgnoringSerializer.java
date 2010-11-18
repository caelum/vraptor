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
package br.com.caelum.vraptor.serialization;

/**
 * Doesn't serialize anything
 * @author Lucas Cavalcanti
 * @author Jose Donizetti
 * @since 3.0.3
 */
public class IgnoringSerializer implements SerializerBuilder {

	public Serializer exclude(String... names) {
		return this;
	}

	public <T> Serializer from(T object) {
		return this;
	}

	public Serializer include(String... names) {
		return this;
	}
	public Serializer recursive() {
		return this;
	}

	public void serialize() {
	}

	public <T> Serializer from(T object, String alias) {
		return this;
	}

}
