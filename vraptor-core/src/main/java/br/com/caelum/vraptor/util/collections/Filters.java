package br.com.caelum.vraptor.util.collections;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.base.Predicate;

public class Filters {

	public static Predicate<Interceptor> accepts(final ResourceMethod method) {
		return new Predicate<Interceptor>() {
			public boolean apply(Interceptor interceptor) {
				return interceptor.accepts(method);
			}
		};
	}
}
