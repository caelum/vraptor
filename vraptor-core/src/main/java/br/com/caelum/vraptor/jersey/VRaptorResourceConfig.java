package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.application.DispatcherFactory;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * A vraptor adaptor to a Jersey Resource configuration.
 * 
 * @author guilherme silveira
 */
public class VRaptorResourceConfig extends WebComponent {

	protected ResourceConfig getDefaultResourceConfig(
			Map<String, Object> props, WebConfig wc) throws ServletException {
		ResourceConfig config = super.getDefaultResourceConfig(props, wc);
//		config.getSingletons().add(new FakeMethodDispatchProvider());
		return config;
	}

	protected ContainerRequest createRequest(WebApplication app,
			HttpServletRequest request, URI baseUri, URI requestUri)
			throws IOException {
		ContainerRequest container = super.createRequest(app, request, baseUri,
				requestUri);
		injectRequest(request, container);
		return container;
	}

	private void injectRequest(HttpServletRequest request,
			ContainerRequest container) {
		container.getProperties().put(DefaultJersey.REQUEST, request);
	}
	
	protected WebApplication create() {
		return new WebApplicationImpl() {
			protected DispatcherFactory getDispatcherFactory() {
				final DispatcherFactory delegate = super.getDispatcherFactory();
				return new DispatcherFactory() {

					public RequestDispatcher getDispatcher(
							AbstractResourceMethod method) {
						return new FakeMethodDispatchProvider(delegate).create(method);
					}

					public Set<ResourceMethodDispatchProvider> getDispatchers() {
						return null;
					}
					
				};
			}
		};
	}
}
