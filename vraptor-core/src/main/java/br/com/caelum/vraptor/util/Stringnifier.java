/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Stringnifier {

	static String argumentsToString(Class<?>[] parameterTypes) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parameterTypes.length; i++) {
			builder.append(parameterTypes[i].getSimpleName());
			if (i != parameterTypes.length - 1)
				builder.append(", ");
		}
		return "(" + builder.toString() + ")";
	}

	public static String simpleNameFor(AccessibleObject object) {
		if (object instanceof Method) {

			Method method = (Method) object;
			return method.getDeclaringClass().getSimpleName() + "." + method.getName()
					+ argumentsToString(method.getParameterTypes());
		}
		if (object instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) object;
			return constructor.getDeclaringClass().getSimpleName() + argumentsToString(constructor.getParameterTypes());
		}
		throw new IllegalArgumentException("can only strignify constructors and methods");

	}
}
