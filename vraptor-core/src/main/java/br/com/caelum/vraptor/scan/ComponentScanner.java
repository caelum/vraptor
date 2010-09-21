package br.com.caelum.vraptor.scan;

import java.util.Collection;

public interface ComponentScanner {

	/**
	 * Scan all classes in WEB-INF/classes and all classes from 
	 * JARs from WEB-INF/lib belonging to specific packages. 
	 * 
	 * @param basePackages Base packages to register JARs.
	 * @return All class names found.
	 */
	Collection<String> scan(ClasspathResolver resolver);

}