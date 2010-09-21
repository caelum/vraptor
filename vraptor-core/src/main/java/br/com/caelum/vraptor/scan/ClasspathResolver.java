package br.com.caelum.vraptor.scan;

import java.net.URL;
import java.util.List;

/**
 * A helper to help locate WEB-INF/classes and all base packages to be scanned
 * 
 * @author Sérgio Lopes
 */
public interface ClasspathResolver {

	/**
	 * Where is WEB-INF/classes?
	 * 
	 * @return WEB-INF/classes location
	 */
	URL findWebInfClassesLocation();

	/**
	 * Find all base packages, including the ones using the new META-INF configuration
	 * @return an array of base packages
	 */
	List<String> findBasePackages();

}