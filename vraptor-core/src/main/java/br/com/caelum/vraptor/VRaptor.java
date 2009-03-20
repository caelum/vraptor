package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.PicoBasedContainer;
import br.com.caelum.vraptor.resource.ResourceLocator;

public class VRaptor implements Filter {

	private Container container;

	public void destroy() {
		container.stop();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
	}

	public void init(FilterConfig cfg) throws ServletException {
		this.container = new PicoBasedContainer(cfg.getServletContext());
		container.withA(ResourceLocator.class).loadAll();
		container.start();
	}

}
