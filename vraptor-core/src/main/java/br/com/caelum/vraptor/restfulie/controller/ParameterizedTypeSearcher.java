/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
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

package br.com.caelum.vraptor.restfulie.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class ParameterizedTypeSearcher {

	private ParameterizedType executeFor(Class<?> control, Class<?> baseType) {
		if(baseType.equals(Object.class)) {
			throw new IllegalStateException(
					"Unable to detect which state control it is because "
							+ control.getClass()
							+ " does not implement StateControl at all.");
		}
		Type[] interfaces = baseType.getGenericInterfaces();
		for (Type type : interfaces) {
			if (!(type instanceof ParameterizedType)) {
				throw new IllegalStateException(
						"Unable to detect which state control it is because "
								+ control.getClass()
								+ " does not implement StateControl of an specific type");
			}
			ParameterizedType parameterized = (ParameterizedType) type;
			if(parameterized.getRawType().equals(ResourceControl.class)) {
				return parameterized;
			}
		}
		return executeFor(control, baseType.getSuperclass());
	}

	public ParameterizedType search(Class<?> at) {
		return executeFor(at, at);
	}

}
