/**
 *
 */
package br.com.caelum.vraptor.ioc.guice;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

final class AllImplementationsProvider<T> implements Provider<List<T>> {
	private List<Class<? extends T>> types = new ArrayList<Class<? extends T>>();
	private Injector injector;
	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
	}
	public void addType(Class<? extends T> type) {
		types.add(type);
	}
	public List<T> get() {
		List<T> instances = new ArrayList<T>();
		for (Class<? extends T> t : types) {
			instances.add(injector.getInstance(t));
		}
		return instances;
	}
}