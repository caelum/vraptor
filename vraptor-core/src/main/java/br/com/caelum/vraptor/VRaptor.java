package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.pico.PicoBasedContainer;
import br.com.caelum.vraptor.resource.ResourceLocator;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class VRaptor implements Filter {

	private Container container;

	public void destroy() {
		container.stop();
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = null;
        HttpServletResponse response = null;
        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) res;
        } catch (ClassCastException e) {
            throw new ServletException("VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.", e);
        }

        UrlToResourceTranslator translator = container
				.instanceFor(UrlToResourceTranslator.class);
		ResourceMethod method = translator.translate(request);
		if (method == null) {
			response.setStatus(404);
			response.getWriter().println("resource not found");
			return;
		}

		container.prepareFor(method, request, response).execute(method);

	}

	public void init(FilterConfig cfg) throws ServletException {
		this.container = new PicoBasedContainer(cfg.getServletContext());
		container.instanceFor(ResourceLocator.class).loadAll();
		container.start();
	}

}
