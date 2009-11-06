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
package br.com.caelum.vraptor.deserialization;

import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

/**
 *
 * @author Lucas Cavalcanti, Ricardo Nakamura
 *
 */
@ApplicationScoped
public class DefaultDeserializers implements Deserializers {

	private final Map<String, Class<? extends Deserializer>> deserializers = new HashMap<String, Class<? extends Deserializer>>();

	public DefaultDeserializers() {
		for (Class<? extends Deserializer> type : BaseComponents.getDeserializers()) {
			register(type);
		}
	}
	public Deserializer deserializerFor(String contentType, Container container) {
		if (!deserializers.containsKey(contentType)) {
			throw new VRaptorException("There is no deserializer for the content type " + contentType);
		}
		return container.instanceFor(deserializers.get(contentType));
	}

	public void register(Class<? extends Deserializer> type) {
		if (!type.isAnnotationPresent(Deserializes.class)) {
			throw new IllegalArgumentException("You must annotate your deserializers with @Deserializes");
		}

		String[] contentTypes = type.getAnnotation(Deserializes.class).value();

		for (String contentType : contentTypes) {
			deserializers.put(contentType, type);
		}
	}

}
