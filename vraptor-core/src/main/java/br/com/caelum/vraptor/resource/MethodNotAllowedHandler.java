package br.com.caelum.vraptor.resource;

import java.util.EnumSet;

import br.com.caelum.vraptor.core.RequestInfo;

/**
 * Handler for Method Not Allowed Http Status (405).
 *
 * @author Lucas Cavalcanti
 * @author Caio Filipini
 */
public interface MethodNotAllowedHandler {

	/**
	 * Denies current request due to method not allowed.
	 * @param request
	 * @param allowedMethods
	 */
	void deny(RequestInfo request, EnumSet<HttpMethod> allowedMethods);

}
