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
package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.util.StringUtils;

/**
 * Default implementation for {@link TypeNameExtractor}.
 * It decapitalizes the name of the type, or if the type is a generic collection,
 * uses the decapitalized name of generic type plus 'List'.
 *
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
@ApplicationScoped
public class DefaultTypeNameExtractor implements TypeNameExtractor {

	public String nameFor(Type generic) {
		if (generic instanceof ParameterizedType) {
			return nameFor((ParameterizedType) generic);
		}

		if (generic instanceof WildcardType) {
			return nameFor((WildcardType) generic);
		}

		if (generic instanceof TypeVariable<?>) {
			return nameFor(((TypeVariable<?>) generic));
		}

		return nameFor((Class<?>) generic);
	}

	private String nameFor(Class<?> raw) {
		if (raw.isArray()) {
			return nameFor(raw.getComponentType()) + "List";
		}

		String name = raw.getSimpleName();

		return StringUtils.lowercaseFirst(name);
	}

	private String nameFor(TypeVariable<?> variable) {
		return StringUtils.lowercaseFirst(variable.getName());
	}

	private String nameFor(WildcardType wild) {
		if ((wild.getLowerBounds().length != 0)) {
			return nameFor(wild.getLowerBounds()[0]);
		} else {
			return nameFor(wild.getUpperBounds()[0]);
		}
	}

	private String nameFor(ParameterizedType type) {
		Class<?> raw = (Class<?>) type.getRawType();
		if (Collection.class.isAssignableFrom(raw)) {
			return nameFor(type.getActualTypeArguments()[0]) + "List";
		}
		return nameFor(raw);
	}

}
