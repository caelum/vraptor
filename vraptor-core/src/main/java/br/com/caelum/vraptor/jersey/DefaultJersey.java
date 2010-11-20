package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher;
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

	// JerseyResourceLookupInterceptor: check if its a jersey component
	// if jersey, just save the rules and next interceptor
	// if not, delegate to typical vrapto

	// private ContextProvider myContextProvider = new DefaultContextProvider()
	// {
	// public WebApplicationContext provide(
	// WebApplicationImpl application,
	// ContainerRequest request,
	// ContainerResponse response) {
	// request.getProperties().put("...", "...");
	// };
	// };

	static final class HttpReqResDispatcher extends
			ResourceJavaMethodDispatcher {
		HttpReqResDispatcher(AbstractResourceMethod abstractResourceMethod) {
			super(abstractResourceMethod);
		}

		public void _dispatch(Object resource, HttpContext context)
				throws IllegalAccessException, InvocationTargetException {
			// just pretend
			context.getProperties().put("...", "...");
			method
					.invoke(resource, context.getRequest(), context
							.getResponse());
		}
	}

	// better pq nao vai rodar as coisas menores dele, vai direto pra onde tem
	// que ir
	// config.register(InvokeNextInterceptorResourceMethodDispatchProvider.class)

	// private WebApplication app = new WebApplicationImpl() {
	// protected UriRule getRootRulesFor(
	// RulesMap<UriRule> rootRules) {
	// return new CheckButNotExecuteRule(rootRules);
	// };
	// };
	private final ServletContext context;
	private final WebComponent webComponent;

	public DefaultJersey(ServletContext context, FilterConfig config)
			throws ServletException {
		this.context = context;
		this.webComponent = new VRaptorResourceConfig();
		this.webComponent.init(new WebFilterConfig(config));
	}

	@SuppressWarnings("unchecked")
	public JerseyResourceComponentMethod findComponent(InterceptorStack stack,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final AtomicBoolean foured = new AtomicBoolean(false);
		HttpServletResponseWrapper fakeResponse = new HttpServletResponseWrapper(
				(HttpServletResponse) resp) {

			public void sendError(int sc, String msg) throws IOException {
				if (sc == 404) {
					foured.set(true);
				}
			}

			public void sendError(int sc) throws IOException {
				if (sc == 404) {
					foured.set(true);
				}
			}

			public void setStatus(int sc, String sm) {
				if (sc == 404) {
					foured.set(true);
				}
			}

			public void setStatus(int sc) {
				if (sc == 404) {
					foured.set(true);
				}
			}

		};

		final RequestUriParser parser = new RequestUriParser(req, fakeResponse,
				webComponent);

		final URI baseUri = parser.getBaseUri();
		final URI requestUri = parser.getRequestUriFor();

		webComponent.service(baseUri, requestUri, req, fakeResponse);
		if (foured.get()) {
			return null;
		}

		// DO something avoiding thread local to check if it was ok
		Iterator<UriRule> rules = (Iterator<UriRule>) req
				.getAttribute(CheckButNotExecuteRule.RULES_FOUND);
		return new JerseyResourceComponentMethod(rules);
	}

}
