
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.DefaultStaticContentHandler;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {
    private ContainerProvider provider;
    private ServletContext servletContext;

    private StaticContentHandler staticHandler;

    private static final Logger logger = LoggerFactory.getLogger(VRaptor.class);

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

        HttpServletRequest baseRequest = (HttpServletRequest) req;
        HttpServletResponse webResponse = (HttpServletResponse) res;

        if (staticHandler.requestingStaticFile(baseRequest)) {
            staticHandler.deferProcessingToContainer(chain, baseRequest, webResponse);
            return;
        }

        VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);

        RequestInfo request = new RequestInfo(servletContext, mutableRequest, webResponse);
        provider.provideForRequest(request, new Execution<Object>() {
            public Object insideRequest(Container container) {
                container.instanceFor(RequestExecution.class).execute();
                return null;
            }
        });
    }

    public void init(FilterConfig cfg) throws ServletException {
        servletContext = cfg.getServletContext();
        BasicConfiguration config = new BasicConfiguration(servletContext);
        init(config.getProvider(), new DefaultStaticContentHandler(servletContext));
        logger.info("VRaptor 3 successfuly initialized");
    }

    void init(ContainerProvider provider, StaticContentHandler handler) {
        this.provider = provider;
        this.staticHandler = handler;
        this.provider.start(servletContext);
    }

}
