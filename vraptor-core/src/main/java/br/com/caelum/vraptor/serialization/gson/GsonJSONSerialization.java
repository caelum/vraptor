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
package br.com.caelum.vraptor.serialization.gson;

import br.com.caelum.vraptor.http.LateResponseCommitHandler;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.Serializee;

/**
 * Gson implementation for JSONSerialization
 *
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Component
public class GsonJSONSerialization implements JSONSerialization {

	protected final LateResponseCommitHandler responseHandler;

	protected final TypeNameExtractor extractor;

	protected final ProxyInitializer initializer;

	protected final VRaptorGsonBuilder builder;

	protected final Serializee serializee;

	public GsonJSONSerialization(LateResponseCommitHandler responseHandler, TypeNameExtractor extractor,
			ProxyInitializer initializer, VRaptorGsonBuilder builder, Serializee serializee) {
		this.responseHandler = responseHandler;
		this.extractor = extractor;
		this.initializer = initializer;

		this.serializee = serializee;
		this.builder = builder;
	}

	public boolean accepts(String format) {
		return "json".equals(format);
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

	public <T> Serializer from(T object, String alias) {
		return getSerializer().from(object, alias);
	}

	protected SerializerBuilder getSerializer() {
		return new GsonSerializer(builder, responseHandler, extractor, initializer, serializee);
	}

	/**
	 * You can override this method for configuring Driver before serialization
	 */
	public <T> NoRootSerialization withoutRoot() {
		builder.setWithoutRoot(true);
		return this;
	}

	public JSONSerialization indented() {
		builder.indented();
		return this;
	}
}