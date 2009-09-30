
package br.com.caelum.vraptor.http.route;

import br.com.caelum.vraptor.http.MutableRequest;

public interface ParametersControl {

	/**
	 * wether the uri matches this uri
	 */
	boolean matches(String uri);

	/**
	 * creates a uri based on those parameter values
	 */
	String fillUri(Object params);

	/**
	 * Inserts parameters extracted from the uri into the request parameters.
	 */
	void fillIntoRequest(String uri, MutableRequest request);

	/**
	 * Applies a list of values to 
	 * @param values
	 * @return
	 */
	String apply(String[] values);

}
