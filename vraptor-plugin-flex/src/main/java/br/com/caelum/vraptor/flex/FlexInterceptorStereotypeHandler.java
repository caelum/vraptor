package br.com.caelum.vraptor.flex;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

@ApplicationScoped
@Component
public class FlexInterceptorStereotypeHandler implements StereotypeHandler {
	private final InterceptorsStack stack;

	public FlexInterceptorStereotypeHandler(InterceptorsStack stack) {
		this.stack = stack;
	}

	public void handle(Class<?> type) {
		stack.add(type);
	}

	public Class<? extends Annotation> stereotype() {
		return FlexIntercepts.class;
	}

}
