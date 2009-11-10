package br.com.caelum.vraptor.rest;


/**
 * Resources implementing this interface will be serialized with their links.
 * 
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 * 
 */
public interface StateControl<T> {

	/**
	 * Returns a list of controllers to be intercepted.
	 */
	Class[] getControllers();

	/**
	 * Given its id (retrieved from the request parameter %id%), returns an
	 * element from the database or null if its not found.
	 */
	T retrieve(String id);

}
