package br.com.caelum.vraptor.ioc.fixture;

import java.util.Set;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;

@Component
@ApplicationScoped
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
	}

}
