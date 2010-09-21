package br.com.caelum.vraptor.scan;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * A dynamic impl of DynamicWebAppBootstrap for use in dev time.
 * 
 * @author Sérgio Lopes
 */
public class DynamicWebAppBootstrap implements WebAppBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(DynamicWebAppBootstrap.class);
	private final Collection<String> classNames;

	public DynamicWebAppBootstrap(Collection<String> classNames) {
		this.classNames = classNames;
	}
	
	public void configure(ComponentRegistry registry) {
		for (String className : classNames) {
			logger.trace("Registering class {}", className);

			try {
				Class<?> clazz = Class.forName(className);
				registry.deepRegister(clazz);
			} catch (ClassNotFoundException e) {
				throw new ScannerException("Error while registering classes", e);
			}
		}
	}

}
