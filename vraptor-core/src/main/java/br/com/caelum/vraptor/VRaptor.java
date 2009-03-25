package br.com.caelum.vraptor;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.pico.PicoBasedContainer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * VRaptor entry point.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {

    private Container container;
    private ServletContext servletContext;

    public void destroy() {
        container.stop();
        container = null;
        servletContext = null;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException(
                    "VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        VRaptorRequest vraptorRequest = new VRaptorRequest(servletContext, request, response);
        //container.getContainerProvider().provide(vraptorRequest).instanceFor(RequestExecution.class).execute();
    }

    public void init(FilterConfig cfg) throws ServletException {
        servletContext = cfg.getServletContext();
        this.container = new PicoBasedContainer(servletContext);
        // container = new SpringBasedContainer(servletContext);
        container.start();
    }

}
