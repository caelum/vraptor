
package br.com.caelum.vraptor.http.route;

import br.com.caelum.vraptor.VRaptorException;

/**
 * Illegal routes might be created under different situations. THe most common
 * is to invoke the is() method but forgetting to invoke the attached method.
 *
 * @author guilherme silveira
 */
public class IllegalRouteException extends VRaptorException {

	/**
	 * Random
	 */
	private static final long serialVersionUID = -3854707059890751018L;

	public IllegalRouteException(String msg) {
		super(msg);
	}

}
