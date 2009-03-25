package br.com.caelum.vraptor.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;

/**
 * VRaptors servlet context init parameter configuration reader.
 * 
 * @author Guilherme Silveira
 */
public class BasicConfiguration {

    static final String CONTAINER_PROVIDER = "br.com.caelum.vraptor.container_provider";

    private final ServletContext servletContext;

    public BasicConfiguration(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ContainerProvider getProvider() throws ServletException {
        String provider = servletContext.getInitParameter(CONTAINER_PROVIDER);
        if (provider == null) {
            provider = PicoProvider.class.getName();
        }
        try {
            return (ContainerProvider) Class.forName(provider).newInstance();
        } catch (InstantiationException e) {
            throw new ServletException(e);
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }

}
