package br.com.caelum.vraptor.resource;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class DefaultMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, EnumSet<HttpMethod> allowedMethods) {
		request.getResponse().addHeader(
				"Allow", allowedMethods.toString().replaceAll("\\[|\\]", ""));
		try {
			request.getResponse().sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (IOException e) {
			throw new InterceptionException(e);
		}
	}

}
