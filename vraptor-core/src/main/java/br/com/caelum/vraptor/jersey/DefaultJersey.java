package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.net.URI;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

import com.sun.jersey.server.impl.application.ContextProvider;
import com.sun.jersey.server.impl.application.DefaultContextProvider;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.servlet.RequestUriParser;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebFilterConfig;

/**
 * A default jersey web component handler. Dispatches calls to jerseys
 * components faking vraptor's process throughout Jersey's process, thus
 * supporting JAX-RS.
 * 
 * @author guilherme silveira
 */
@Component
@ApplicationScoped
public class DefaultJersey implements Jersey {

	public static final String INTERCEPTOR_STACK = Jersey.class.getPackage()
			+ ".stack";

	static final String FOUR_O_FOURED = Jersey.class.getPackage().getName()
			+ ".404";

	public static final String REQUEST = Jersey.class.getPackage().getName()
			+ ".request";

	// private WebApplication app = new WebApplicationImpl() {
	// protected UriRule getRootRulesFor(
	// RulesMap<UriRule> rootRules) {
	// return new CheckButNotExecuteRule(rootRules);
	// };
	// };
	private final WebComponent webComponent;

	public DefaultJersey(FilterConfig config) throws ServletException {
		this.webComponent = new VRaptorResourceConfig();
		this.webComponent.init(new WebFilterConfig(config));
	}

	public boolean findComponent(InterceptorStack stack,
			final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpServletResponseWrapper fake = new FourOFourResponseWrapper(req,
				(HttpServletResponse) resp);

		RequestUriParser parser = new RequestUriParser(req, fake, webComponent);

		URI base = parser.getBaseUri();
		URI requestUri = parser.getRequestUriFor();

		req.setAttribute(INTERCEPTOR_STACK, stack);
		webComponent.service(base, requestUri, req, fake);
		return isMine(req);
		// if (req.getAttribute(FOUR_O_FOURED) != null) { return null; }

		// DO something avoiding thread local to check if it was ok
		// Iterator<UriRule> rules = (Iterator<UriRule>) req
		// .getAttribute(CheckButNotExecuteRule.RULES_FOUND);
		// return new JerseyDispatcher();
	}

	public Object instantiate(HttpServletRequest request) {
		return request.getAttribute(FakeMethodDispatchProvider.RESOURCE_TO_USE);
	}

	public boolean isMine(HttpServletRequest request) {
		return request.getAttribute(FakeMethodDispatchProvider.METHOD_TO_EXECUTE) != null;
	}

}
