package br.com.caelum.vraptor.ioc.pico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Accepts all classes implementing ComponentFactory 
 * 
 * @author Sérgio Lopes
 */
@ApplicationScoped
public class ComponentFactoryAcceptor implements Acceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(ComponentFactoryAcceptor.class);
	private final ComponentFactoryRegistry registry;

	public ComponentFactoryAcceptor(ComponentFactoryRegistry registry) {
		this.registry = registry;
	}
	
	public void analyze(Class<?> type) {
		if (ComponentFactory.class.isAssignableFrom(type) && type.isAnnotationPresent(Component.class)) {

			if (logger.isTraceEnabled())
				logger.trace("ComponentFactory found: " + type.getName());
			
			@SuppressWarnings("unchecked")
			Class<ComponentFactory<?>> clazz = (Class<ComponentFactory<?>>) type.asSubclass(ComponentFactory.class);
			
			registry.register(clazz);
		}
	}

}
