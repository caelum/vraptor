
package br.com.caelum.vraptor.http.route;

import br.com.caelum.vraptor.VRaptorException;

/**
 * A route was not found for the specified parameters.
 * 
 * @author guilherme silveira
 */
public class RouteNotFoundException extends VRaptorException {

	public RouteNotFoundException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 606801838930057251L;

}
