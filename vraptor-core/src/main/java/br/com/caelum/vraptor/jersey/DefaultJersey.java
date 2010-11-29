package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.net.URI;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.Path;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

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

	public static final String REQUEST = Jersey.class.getPackage().getName()
			+ ".request";

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
	}

	public Object instantiate(HttpServletRequest request) {
		return request.getAttribute(JerseyDispatcher.RESOURCE_TO_USE);
	}

	public boolean isMine(HttpServletRequest request) {
		return request.getAttribute(JerseyDispatcher.METHOD_TO_EXECUTE) != null;
	}

	public void execute(HttpServletRequest request, Object instance) {
		JerseyDispatcher disp = (JerseyDispatcher) request.getAttribute(JerseyDispatcher.DISPATCHER);
		disp.execute(instance, request);
	}

	@SuppressWarnings("unchecked")
	public boolean shouldInstantiate(Class type) {
		return type.isAnnotationPresent(Path.class);
	}

}
