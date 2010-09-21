package br.com.caelum.vraptor.scan;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * Creates the right WebAppBootstrap
 * 
 * @author Sérgio Lopes
 */
public class WebAppBootstrapFactory {

	private static final Logger logger = LoggerFactory.getLogger(WebAppBootstrapFactory.class);
	
	/**
	 * Returns the WebAppBootstrap for this web application
	 * 
	 * @param servletContext
	 * @return
	 */
	public WebAppBootstrap create(BasicConfiguration config) {
		try {
			// try the generated static file first
			Class<?> clazz = Class.forName(WebAppBootstrap.STATIC_BOOTSTRAP_NAME);
			
			logger.info("Found a static WebAppBootstrap; using it and skipping cp scanning.");
			return (WebAppBootstrap) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			logger.info("No static WebAppBootstrap found.");
			
			// if scanning is disabled, return a null object
			if (!config.isClasspathScanningEnabled()) {
				logger.info("Classpath scanning is disabled.");
				return new WebAppBootstrap() {
					public void configure(ComponentRegistry registry) { /* does nothing */ }
				};
			}
			
			// dinamically scan the classpath if there's no static cache generated
			ClasspathResolver resolver = new WebBasedClasspathResolver(config.getServletContext());
			
			logger.trace("Start classpath scanning");
			ComponentScanner scanner = new ScannotationComponentScanner();
			Collection<String> classNames = scanner.scan(resolver);
			logger.trace("End classpath scanning");
			
			WebAppBootstrap wab = new DynamicWebAppBootstrap(classNames);
			return wab;
		} catch (Exception e) {
			throw new ScannerException("Error while creating the StaticWebAppBootstrap", e);
		}
	}
}
