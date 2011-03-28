package br.com.caelum.vraptor.flex;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.InterceptorHandlerFactory;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;

@Component
@ApplicationScoped
public class InterceptorsStack {
	private final Container container;
	private final InterceptorHandlerFactory interceptorHandlerFactory;

	public InterceptorsStack(Container container, InterceptorHandlerFactory interceptorHandlerFactory) {
		this.container = container;
		this.interceptorHandlerFactory = interceptorHandlerFactory;
	}

	private List<Class<? extends Interceptor>> types = new ArrayList<Class<? extends Interceptor>>();

	public void add(Class<?> type) {
		this.types.add((Class<? extends Interceptor>) type);
	}

	public InterceptorStack createStack() {
		DefaultInterceptorStack stack = new DefaultInterceptorStack(interceptorHandlerFactory);

		for (Class<? extends Interceptor> type : types) {
			stack.add(type);
		}
		return stack;
	}

}
