package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * A simple expression evaluator.Based on vraptor2 code.
 * 
 * @author Guilherme Silveira
 */
public class ExpressionEvaluator {

	private final Map<String, Method> cache = new HashMap<String, Method>();

	public String parseExpression(String expression, VRaptorRequest request) throws ExpressionEvaluationException {
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

	private String evaluate(String expression, VRaptorRequest request) throws ExpressionEvaluationException {
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

	private Object findAttribute(VRaptorRequest request, String key) {
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
		String key = obj.getClass().getName().toString() + "." + property;
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
