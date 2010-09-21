package br.com.caelum.vraptor.scan;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * Register all application components.
 * 
 * @author Sérgio Lopes
 */
public interface WebAppBootstrap {

	/**
	 * Configure all components using ComponentRegistry
	 * @param registry
	 */
	void configure (ComponentRegistry registry);
	
	/**
	 * Class name of the generated WebAppBootStrap impl
	 */
	String STATIC_BOOTSTRAP_NAME = "br.com.caelum.vraptor.generated.StaticProjectBootstrap";
}
