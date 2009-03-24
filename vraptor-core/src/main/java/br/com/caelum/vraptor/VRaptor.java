package br.com.caelum.vraptor;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.pico.PicoBasedContainer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * VRaptor 3 entrance point.
 *
 * @author Guilherme Silveira
 */
public class VRaptor implements Filter {

    private Container container;

    public void destroy() {
        container.stop();
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException(
                    "VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        container.instanceFor(RequestExecution.class).execute(new VRaptorRequest(request, response));
    }

    public void init(FilterConfig cfg) throws ServletException {
        this.container = new PicoBasedContainer(cfg.getServletContext());
        // container = new SpringBasedContainer(cfg.getServletContext());
        container.start();
    }

}
