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
package br.com.caelum.vraptor.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation for {@link Outjector}.
 * It uses a chain of maps that mimics objects on request.
 *
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
public class DefaultOutjector implements Outjector {

	private static final String DELIMITERS = "(\\[|\\]|\\.)";
	private static final String DELIM_CHARS = "\\[\\]\\.";
	private final HttpServletRequest request;

	public DefaultOutjector(HttpServletRequest request) {
		this.request = request;
	}

	@SuppressWarnings("unchecked")
	public void outjectRequestMap() {
		Set<String> paramNames = request.getParameterMap().keySet();

		for (String paramName : paramNames) {
			if (isSimple(paramName)) {
				request.setAttribute(paramName, request.getParameter(paramName));
			} else {
				String baseName = extractBaseParamName(paramName);
				if (request.getAttribute(baseName) == null) {
					request.setAttribute(baseName, new HashMap<String, Object>());
				}
				processComplexParameter(paramName, stripBaseName(paramName, baseName), castMap(request.getAttribute(baseName)));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private Map<String, Object> castMap(final Object object) {
		if (object instanceof Map<?,?>) {
			return (Map<String, Object>) object ;
		}
		if (object instanceof String) {
			return new HashMap<String, Object>() {
				@Override
				public String toString() {
					return object.toString();
				}
			};
		}
		throw new IllegalStateException("Some request parameter has the same name as a request attribute. " +
				"It shouldn't happen, please report this bug.");
	}

	private String stripBaseName(String paramName, String baseName) {
		return paramName.replaceFirst("^" + baseName + DELIMITERS + "+", "");
	}

	private void processComplexParameter(String fullName, String paramName, Map<String, Object> parent) {
		if (isSimple(paramName)) {
			parent.put(paramName.replaceFirst("\\]$", ""), request.getParameter(fullName));
		} else {
			String baseName = extractBaseParamName(paramName);
			if (parent.get(baseName) == null) {
				parent.put(baseName, new HashMap<String, Object>());
			}
			processComplexParameter(fullName, stripBaseName(paramName, baseName), castMap(parent.get(baseName)));
		}
	}

	private boolean isSimple(String paramName) {
		return paramName.matches("[^" + DELIM_CHARS + "]+?\\]?");
	}

	private String extractBaseParamName(String paramName) {
		return paramName.replaceFirst("^([^" + DELIM_CHARS + "]+)[" + DELIM_CHARS + "].*$", "$1");
	}
}
