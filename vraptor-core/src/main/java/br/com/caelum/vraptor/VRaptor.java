package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * VRaptor entry point. Provider configuration through init parameter
 * 
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {
    private ContainerProvider provider;
    private ServletContext servletContext;

    public void destroy() {
        provider.stop();
        provider = null;
        servletContext = null;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException(
                    "VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
        }

        HttpServletRequest webRequest = (HttpServletRequest) req;
        HttpServletResponse webResponse = (HttpServletResponse) res;

        VRaptorRequest request = new VRaptorRequest(servletContext, webRequest, webResponse);
        provider.provide(request).instanceFor(RequestExecution.class).execute();
    }

    public void init(FilterConfig cfg) throws ServletException {
        servletContext = cfg.getServletContext();
        BasicConfiguration config = new BasicConfiguration(servletContext);
        this.provider = config.getProvider();
        this.provider.start(servletContext);
    }

}
