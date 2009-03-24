package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.http.RequestContainer;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class PicoBasedRequestContainer implements RequestContainer {
	
	private final MutablePicoContainer container;

	public PicoBasedRequestContainer(MutablePicoContainer parent, ResourceMethod method, HttpServletRequest request, HttpServletResponse response) {
        // TODO scan classpath and find @Scope(REQUEST) annotated components
	    this.container = new PicoBuilder(parent).withCaching().build();
		this.container.addComponent(this).addComponent(request).addComponent(response).addComponent(method);
		this.container.addComponent(DefaultInterceptorStack.class).addComponent(DefaultRequestExecution.class);
		this.container.addComponent(PicoBasedInstantiateInterceptor.class);
	}

	public void register(Object instance) {
		this.container.addComponent(instance);
	}

	public <T> T withA(Class<T> type) {
		return container.getComponent(type);
	}

}
