package br.com.caelum.vraptor.jersey;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

/**
 * A provider capable of dealing with Jersey and VRaptor3 components at the same
 * time.
 * 
 * @author Guilherme Silveira
 */
public class Provider extends SpringProvider {

	protected void registerCustomComponents(ComponentRegistry registry) {
		super.registerCustomComponents(registry);
		registry.register(JerseyResourceLookupInterceptor.class,
				JerseyResourceLookupInterceptor.class);
		registry.register(RequestExecution.class, RequestStack.class);
	}

}
