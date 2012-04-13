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

package br.com.caelum.vraptor.http.route;

import static com.google.common.base.Objects.equal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A strategy which returns the resource method based on a url pattern.
 *
 * @author guilherme silveira
 */
@Deprecated
public class PatternBasedStrategy implements Route {

	private final Logger logger = LoggerFactory.getLogger(PatternBasedStrategy.class);

	private final PatternBasedType type;
	private final PatternBasedType method;
	private final EnumSet<HttpMethod> methods;

	private final ParametersControl control;

	private final int priority;

	public PatternBasedStrategy(ParametersControl control, PatternBasedType type, PatternBasedType method,
			Set<HttpMethod> methods, int priority) {
		this.control = control;
		this.type = type;
		this.method = method;
		this.methods = methods.isEmpty() ? EnumSet.allOf(HttpMethod.class) : EnumSet.copyOf(methods);
		this.priority = priority;
	}

	public boolean canHandle(Class<?> type, Method method) {
		return this.type.matches(type.getName()) && this.method.matches(method.getName());
	}

	public ResourceMethod resourceMethod(MutableRequest request, String uri) {
		control.fillIntoRequest(uri, request);
		String webLogic = request.getParameter("webLogic");
		String webMethod = request.getParameter("webMethod");
		String typeName = type.apply("webLogic", webLogic);
		try {
			DefaultResourceClass resource = new DefaultResourceClass(Class.forName(typeName));
			Method resourceMethod = method(resource.getType(), this.method.apply("webMethod", webMethod));
			return new DefaultResourceMethod(resource, resourceMethod);
		} catch (ClassNotFoundException e) {
            logger.debug("Unable to find type {} for strategy {}", typeName, this);
			throw new IllegalStateException("You must call canHandle before calling this method");
		}
	}

	public EnumSet<HttpMethod> allowedMethods() {
		return methods;
	}

	public boolean canHandle(String uri) {
		return control.matches(uri);
	}

	private Method method(Class<?> type, String methodName) {
		Method[] methods = type.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName) && isEligible(m)) {
				return m;
			}
		}
		if (type.getSuperclass().equals(Object.class)) {
			return null;
		}
		return method(type.getSuperclass(), methodName);
	}

	private boolean isEligible(Method m) {
		return Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers());
	}

	public String urlFor(Class<?> type, Method m, Object... params) {
		return control.apply(new String[] { this.type.extract("webLogic", type.getName()),
				this.method.extract("webMethod", m.getName()) });
	}

	public int getPriority() {
		return this.priority;
	}

	public String getOriginalUri() {
		return control.toString();
	}

	@Override
	public String toString() {
		return String.format("[PatternBasedStrategy: %-50s %-50s %s]", type, method, methods.size() == HttpMethod
				.values().length ? "ALL" : methods);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PatternBasedStrategy other = (PatternBasedStrategy) obj;
		return equal(method, other.method) && equal(methods, other.methods) && equal(type, other.type);
	}
}
