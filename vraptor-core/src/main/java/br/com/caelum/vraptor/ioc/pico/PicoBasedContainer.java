package br.com.caelum.vraptor.ioc.pico;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;

public class PicoBasedContainer implements Container {
	
	private final MutablePicoContainer container;

	public PicoBasedContainer(MutablePicoContainer parent, VRaptorRequest request) {
        // TODO scan classpath and find @Scope(REQUEST) annotated components
	    this.container = new PicoBuilder(parent).withCaching().build();
		this.container.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
		this.container.addComponent(DefaultInterceptorStack.class).addComponent(DefaultRequestExecution.class);
		this.container.addComponent(PicoBasedInstantiateInterceptor.class);
	}

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

}
