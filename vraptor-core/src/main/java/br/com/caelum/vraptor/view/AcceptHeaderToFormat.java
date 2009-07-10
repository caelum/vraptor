package br.com.caelum.vraptor.view;

/**
 * Translate the Accept header to a _format String
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
public interface AcceptHeaderToFormat {

	/**
	 * Get the _format associated with the given Accept header.
	 * Should not return null, return html instead.
	 * 
	 * @param accept The Accept HTTP header
	 * @return The _format String
	 */
	String getFormat(String accept);
	
}
