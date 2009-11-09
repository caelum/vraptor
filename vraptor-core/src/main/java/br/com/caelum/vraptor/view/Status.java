package br.com.caelum.vraptor.view;

/**
 * Allows header related results.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Status {

	public void notFound();

	void header(String key, String value);
	
	void created();

	/**
	 * Sets the header to 201 and sets the location to the server's location + the location content.<br>
	 * created("/order/2") ==> http://localhost:8080/my_context/order/2
	 * @param location
	 */
	void created(String location);
	
	void ok();

}
