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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.util.Stringnifier;

public class DefaultResourceMethod implements ResourceMethod {

	private final ResourceClass resource;
	private final Method method;

	public DefaultResourceMethod(ResourceClass resource, Method method) {
		this.resource = resource;
		this.method = method;
	}

	public static ResourceMethod instanceFor(Class<?> type, Method method) {
		return new DefaultResourceMethod(new DefaultResourceClass(type), method);
	}

	public Method getMethod() {
		return method;
	}

	public ResourceClass getResource() {
		return resource;
	}

	public boolean containsAnnotation(Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}

	@Override
	public String toString() {
		return "[DefaultResourceMethod: "
				+ method.getDeclaringClass().getSimpleName() + "."
				+ method.getName()
				+ Stringnifier.simpleNameFor(method) + "]";
	}

}
