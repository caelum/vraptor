package br.com.caelum.vraptor.restfulie.headers;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.restfulie.RestHeadersHandler;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.resource.Cacheable;

@Component
public class DefaultRestHeadersHandler implements RestHeadersHandler {
	
	private final HttpServletResponse response;

	public DefaultRestHeadersHandler(HttpServletResponse response) {
		this.response = response;
	}

	public void handle(HypermediaResource resource) {
		// TODO implement link headers
		if(Cacheable.class.isAssignableFrom(resource.getClass())) {
			Cacheable cache = (Cacheable) resource;
			response.addHeader("Cache-control","max-age=" + cache.getMaximumAge());
		}
	}

}
