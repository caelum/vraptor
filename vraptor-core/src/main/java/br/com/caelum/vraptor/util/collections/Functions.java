package br.com.caelum.vraptor.util.collections;

import br.com.caelum.vraptor.ioc.Container;

import com.google.common.base.Function;

public class Functions {

	public static <T> Function<Class<? extends T>, ? extends T> instanceWith(final Container container) {
		return new Function<Class<? extends T>, T>() {
			public T apply(Class<? extends T> type) {
				return container.instanceFor(type);
			}
		};
	}
}
