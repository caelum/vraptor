package br.com.caelum.vraptor.util.collections;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.base.Function;

public class Functions {

	public static <T> Function<Class<? extends T>, ? extends T> instanceWith(final Container container) {
		return new Function<Class<? extends T>, T>() {
			public T apply(Class<? extends T> type) {
				return container.instanceFor(type);
			}
		};
	}

	public static Function<Route, ResourceMethod> matches(final String uri, final HttpMethod method, final MutableRequest request) {
		return new Function<Route, ResourceMethod>() {
			public ResourceMethod apply(Route route) {
				return route.matches(uri, method, request);
			}
		};
	}
}
