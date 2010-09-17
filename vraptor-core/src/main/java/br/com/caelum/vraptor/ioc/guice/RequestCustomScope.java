package br.com.caelum.vraptor.ioc.guice;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class RequestCustomScope implements Scope {

	enum NullObject { INSTANCE }

	public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
		final String name = key.toString();
		return new Provider<T>() {
			public T get() {
				HttpServletRequest request = VRaptorRequestHolder.currentRequest().getRequest();
				synchronized (request) {
					Object obj = request.getAttribute(name);
					if (NullObject.INSTANCE == obj) {
						return null;
					}
					@SuppressWarnings("unchecked")
					T t = (T) obj;
					if (t == null) {
						t = creator.get();
						request.setAttribute(name, (t != null) ? t : NullObject.INSTANCE);
					}
					return t;
				}
			}

			@Override
			public String toString() {
				return String.format("%s[%s]", creator, this);
			}
		};
	}

	@Override
	public String toString() {
		return "ServletScopes.REQUEST";
	}
}
