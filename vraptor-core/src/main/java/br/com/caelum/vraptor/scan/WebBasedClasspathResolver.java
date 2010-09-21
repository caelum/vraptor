package br.com.caelum.vraptor.scan;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * A classpath resolver based on ServletContext
 * 
 * @author Sérgio Lopes
 */
public class WebBasedClasspathResolver implements ClasspathResolver {

	private final ServletContext servletContext;
	
	public WebBasedClasspathResolver(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public URL findWebInfClassesLocation() {
		try {
			String webInfClassesDir = servletContext.getRealPath("/WEB-INF/classes");
			if (webInfClassesDir != null) {
				return new URL("file:" + webInfClassesDir + "/");
			} else {
				// try to guess WEB-INF/classes from vraptor.jar location 
				return new StandaloneClasspathResolver().findWebInfClassesLocation();
			}
		} catch (Exception e) { 
			throw new ScannerException("Could not determine WEB-INF/classes location", e);
		}
	}
	
	public List<String> findBasePackages() {
		ArrayList<String> packages = new ArrayList<String>();
		
		// find packages from web.xml
		String packagesParam = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
		if (packagesParam != null) {
			Collections.addAll(packages, packagesParam.trim().split("\\s*,\\s*"));
		}
		
		// find plugin packages
		new StandaloneClasspathResolver().getPackagesFromPluginsJARs(packages);
		return packages;
	}
}
