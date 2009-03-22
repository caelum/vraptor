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
import br.com.caelum.vraptor.ioc.PicoBasedContainer;
import br.com.caelum.vraptor.resource.ResourceLocator;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class VRaptor implements Filter {

	private Container container;

	public void destroy() {
		container.stop();
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		UrlToResourceTranslator translator = container
				.withA(UrlToResourceTranslator.class);
		ResourceMethod method = translator.translate(request);
		if (method == null) {
			response.setStatus(404);
			response.getWriter().println("resource not found");
			return;
		}

		container.prepareFor(request, response).execute(method);

	}

	public void init(FilterConfig cfg) throws ServletException {
		this.container = new PicoBasedContainer(cfg.getServletContext());
		container.withA(ResourceLocator.class).loadAll();
		container.start();
	}

}
