package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

import com.sun.jersey.spi.container.servlet.RequestUriParser;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebFilterConfig;
import com.sun.jersey.spi.uri.rules.UriRule;

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

	public static final String INTERCEPTOR_STACK = Jersey.class.getPackage() + ".stack";

	// private ContextProvider myContextProvider = new DefaultContextProvider()
	// {
	// public WebApplicationContext provide(
	// WebApplicationImpl application,
	// ContainerRequest request,
	// ContainerResponse response) {
	// request.getProperties().put("...", "...");
	// };
	// };

	static final String FOUR_O_FOURED = DefaultJersey.class.getPackage()
			.getName()
			+ ".404";

	// better pq nao vai rodar as coisas menores dele, vai direto pra onde tem
	// que ir
	// config.register(InvokeNextInterceptorResourceMethodDispatchProvider.class)

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

	@SuppressWarnings("unchecked")
	public JerseyResourceComponentMethod findComponent(InterceptorStack stack,
			final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpServletResponseWrapper fake = new FourOFourResponseWrapper(
				req, (HttpServletResponse) resp);

		RequestUriParser parser = new RequestUriParser(req, fake, webComponent);

		URI base = parser.getBaseUri();
		URI requestUri = parser.getRequestUriFor();

		webComponent.service(base, requestUri, req, fake);
		if (req.getAttribute(FOUR_O_FOURED) != null) {
			return null;
		}

		// DO something avoiding thread local to check if it was ok
		Iterator<UriRule> rules = (Iterator<UriRule>) req
				.getAttribute(CheckButNotExecuteRule.RULES_FOUND);
		return new JerseyResourceComponentMethod(rules);
	}

	public Object instantiate(HttpServletRequest request) {
		return null;
	}

	public boolean isMine(HttpServletRequest request) {
		context.getProperties().put(METHOD_TO_EXECUTE, method)
		return false;
	}

}
