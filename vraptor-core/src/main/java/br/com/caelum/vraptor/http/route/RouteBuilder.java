package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.resource.HttpMethod;

public interface RouteBuilder {

	ParameterControlBuilder withParameter(String name);
	public interface ParameterControlBuilder {

		RouteBuilder ofType(Class<?> type);

		RouteBuilder matching(String regex);

	}

	<T> T is(final Class<T> type);

	void is(Class<?> type, Method method);

	/**
	 * Accepts also this http method request. If this method is not invoked, any
	 * http method is supported, otherwise all parameters passed are supported.
	 *
	 * @param method
	 * @return
	 */
	RouteBuilder with(HttpMethod method);

	/**
	 * Changes Route priority
	 *
	 * @param priority
	 * @return
	 */
	RouteBuilder withPriority(int priority);

	Route build();
}