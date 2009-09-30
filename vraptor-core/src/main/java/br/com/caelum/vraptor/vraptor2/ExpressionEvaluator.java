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
package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.core.RequestInfo;

/**
 * A simple expression evaluator.Based on vraptor2 code.
 * 
 * @author Guilherme Silveira
 */
class ExpressionEvaluator {

	private final Map<String, Method> cache = new HashMap<String, Method>();

	public String parseExpression(String expression, RequestInfo request) throws ExpressionEvaluationException {
		boolean in = false;
		int start = 0;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == '$') {
				if (in || expression.length() - 1 == i || expression.charAt(i + 1) != '{') {
					throw new ExpressionEvaluationException("Invalid expression " + expression);
				} else {
					in = true;
					i++;
					start = i + 1;
				}
			} else if (c == '}' && in) {
				in = false;
				result.append(evaluate(expression.substring(start, i), request));
			} else if (!in) {
				result.append(c);
			}
		}
		return result.toString();
	}

	private String evaluate(String expression, RequestInfo request) throws ExpressionEvaluationException {
		if ("".equals(expression)) {
			return "";
		}
		String[] parts = expression.split("\\.");
		Object current = findAttribute(request, parts[0]);
		for (int i = 1; i < parts.length; i++) {
			if (current == null) {
				throw new ExpressionEvaluationException("Invalid redirection using: " + expression);
			}
			current = invokeGetter(current, parts[i]);
		}
		return current == null ? "" : current.toString();
	}

	private Object findAttribute(RequestInfo request, String key) {
		Object value = request.getRequest().getAttribute(key);
		if(value!=null) {
			return value;
		}
		value = request.getRequest().getSession().getAttribute(key);
		if(value!=null) {
			return value;
		}
		return request.getServletContext().getAttribute(key);
	}

	private Object invokeGetter(Object obj, String property) throws ExpressionEvaluationException {
		property = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
		String key = obj.getClass().getName() + "." + property;
		try {
			if (!cache.containsKey(key)) {
				cache.put(key, obj.getClass().getMethod(property));
			}
			return cache.get(key).invoke(obj);
		} catch (Exception e) {
			throw new ExpressionEvaluationException("Invalid expression " + property, e);
		}
	}
}
