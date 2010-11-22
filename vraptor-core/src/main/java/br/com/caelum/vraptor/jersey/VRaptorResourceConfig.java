package br.com.caelum.vraptor.jersey;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebConfig;

/**
 * A vraptor adaptor to a Jersey Resource configuration.
 * 
 * @author guilherme silveira
 */
public class VRaptorResourceConfig extends WebComponent {

	protected ResourceConfig getDefaultResourceConfig(
			Map<String, Object> props, WebConfig wc) throws ServletException {
		ResourceConfig config = super.getDefaultResourceConfig(props, wc);
		config.getSingletons().add(new FakeMethodDispatchProvider());
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
}
