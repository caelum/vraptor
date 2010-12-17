package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

@PrototypeScoped
public class EnhancedRequestExecution implements RequestExecution {

	private final InterceptorRegistry registry;
	private final InterceptorStack stack;

	public EnhancedRequestExecution(InterceptorStack stack, InterceptorRegistry registry) {
		this.stack = stack;
		this.registry = registry;
	}

	public void execute() throws VRaptorException {
		for (Class<? extends Interceptor> interceptor : registry.all()) {
			stack.add(interceptor);
		}
		stack.next(null, null);
	}

}
