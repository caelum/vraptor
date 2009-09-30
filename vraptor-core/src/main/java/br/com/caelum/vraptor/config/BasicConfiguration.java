
package br.com.caelum.vraptor.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.MissingConfigurationException;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

/**
 * VRaptors servlet context init parameter configuration reader.
 *
 * @author Guilherme Silveira
 */
public class BasicConfiguration {

	/**
	 * context parameter that represents the class of IoC provider
	 */
    public static final String CONTAINER_PROVIDER = "br.com.caelum.vraptor.provider";

    /**
     * context parameter that represents the base package(s) of your application
     */
    public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.packages";

    private final ServletContext servletContext;

    public BasicConfiguration(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ContainerProvider getProvider() throws ServletException {
        String provider = servletContext.getInitParameter(CONTAINER_PROVIDER);
        if (provider == null) {
            provider = SpringProvider.class.getName();
        }
        try {
            return (ContainerProvider) Class.forName(provider).getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw new ServletException(e.getCause());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public String getBasePackages() {
    	String packages = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
    	if (packages == null) {
			throw new MissingConfigurationException(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME + " context-param not found in web.xml. " +
					"Set this parameter with your base package");
		}
    	return packages;
    }

}
