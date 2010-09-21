package br.com.caelum.vraptor.scan;

import java.util.Collection;

/**
 * Generates a static WebAppBootstrap implementation with the scanned classes.
 * 
 * @author Sérgio Lopes
 */
public interface BootstrapGenerator {

	void generate(Collection<String> components, ClasspathResolver resolver);

}