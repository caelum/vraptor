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
/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.HttpMethod;

import com.google.common.base.Joiner;

/**
 * Should be used in one of two ways, either configure the type and invoke the
 * method or pass the method (java reflection) object.
 *
 * If not specified, the built route will have the lowest priority (higher value of priority),
 * so will be the last to be used.
 * @author Guilherme Silveira
 */
public class RouteBuilder {
	private final Set<HttpMethod> supportedMethods = new HashSet<HttpMethod>();

	private final Proxifier proxifier;
	private final Logger logger = LoggerFactory.getLogger(RouteBuilder.class);

	private final String originalUri;

	private Route strategy = new NoStrategy();

	private int priority = Integer.MAX_VALUE;

	private final ParameterControlBuilder builder;

	private final TypeFinder finder;

	public RouteBuilder(Proxifier proxifier, TypeFinder finder, String uri) {
		this.proxifier = proxifier;
		this.finder = finder;
		uri = uri.replaceAll("\\*[^\\}]", ".\\*?");
		this.originalUri = uri;
		builder = new ParameterControlBuilder();
	}

	public class ParameterControlBuilder {
		private final Map<String, String> parameters = new HashMap<String, String>();
		private String name;

		private ParameterControlBuilder withParameter(String name) {
			this.name = name;
			return this;
		}

		public RouteBuilder ofType(Class<?> type) {
			parameters.put(name, regexFor(type));
			return RouteBuilder.this;
		}

		private String regexFor(Class<?> type) {
			if (Arrays.asList(Integer.class, Long.class, int.class, long.class, BigInteger.class, char.class, Character.class, Short.class, short.class)
						.contains(type)) {
				return "-?\\d+";
			} else if (Arrays.asList(Double.class, BigDecimal.class, double.class, Float.class, float.class).contains(type)) {
				return "-?\\d*\\.?\\d+";
			} else if (Arrays.asList(Boolean.class, boolean.class).contains(type)){
				return "true|false";
			} else if (Enum.class.isAssignableFrom(type)) {
				return Joiner.on("|").join(type.getEnumConstants());
			}
			return "[^/]+";
		}

		public RouteBuilder matching(String regex) {
			parameters.put(name, regex);
			return RouteBuilder.this;
		}

		private ParametersControl build() {
			return new DefaultParametersControl(originalUri, parameters);
		}
	}

	public ParameterControlBuilder withParameter(String name) {
		return builder.withParameter(name);
	}

	public <T> T is(final Class<T> type) {
		MethodInvocation<T> handler = new MethodInvocation<T>() {
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				boolean alreadySetTheStrategy = !strategy.getClass().equals(NoStrategy.class);
				if (alreadySetTheStrategy) {
					// the virtual machine might be invoking the finalize
					return null;
				}
				is(type, method);
				return null;
			}
		};
		return proxifier.proxify(type, handler);
	}

	public void is(PatternBasedType type, PatternBasedType method) {
		this.strategy = new PatternBasedStrategy(builder.build(), type, method,
				this.supportedMethods, priority);

	}

	public void is(Class<?> type, Method method) {
		addParametersInfo(method);
		this.strategy = new FixedMethodStrategy(originalUri, type, method, this.supportedMethods,
				builder.build(), priority);
		logger.info(originalUri + " --> " + method);
	}

	private void addParametersInfo(Method method) {
		String[] parameters = extractParameters(originalUri);
		Map<String, Class<?>> types = finder.getParameterTypes(method, parameters);
		for (Entry<String, Class<?>> entry : types.entrySet()) {
			if (!builder.parameters.containsKey(entry.getKey())) {
				builder.withParameter(entry.getKey()).ofType(entry.getValue());
			}
		}
		for (String parameter : parameters) {
			String[] split = parameter.split(":");
			if (split.length >= 2 && !builder.parameters.containsKey(parameter)) {
				builder.withParameter(parameter).matching(split[1]);
			}
		}
	}

	private String[] extractParameters(String uri) {
		String startUntilOpenBraces = "^[^\\{]*\\{";
		String or = "|";
		String betweenBraces = "\\}[^\\{]*\\{";
		String closeBracesUntilEnd = "\\}[^\\{]*$";

		return uri.split(startUntilOpenBraces +
				or +
				betweenBraces +
				or +
				closeBracesUntilEnd);
	}

	/**
	 * Accepts also this http method request. If this method is not invoked, any
	 * http method is supported, otherwise all parameters passed are supported.
	 *
	 * @param method
	 * @return
	 */
	public RouteBuilder with(HttpMethod method) {
		this.supportedMethods.add(method);
		return this;
	}

	/**
	 * Changes Route priority
	 * @param priority
	 * @return
	 */
	public RouteBuilder withPriority(int priority) {
		this.priority = priority;
		return this;
	}


	public Route build() {
		if (strategy instanceof NoStrategy) {
			throw new IllegalRouteException("You have created a route, but did not specify any method to be invoked: "
					+ originalUri);
		}
		return strategy;
	}

	@Override
	public String toString() {
		if (supportedMethods.isEmpty()) {
			return String.format("<< Route: %s => %s >>", originalUri, this.strategy.toString());
		}
		return String.format("<< Route: %s %s=> %s >>", originalUri, supportedMethods, this.strategy.toString());
	}

}
