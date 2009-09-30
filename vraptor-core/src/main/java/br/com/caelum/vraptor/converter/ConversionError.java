
package br.com.caelum.vraptor.converter;

/**
 * A conversion error. The message should already be i18n.
 *
 * @author guilherme silveira
 */
public class ConversionError extends Error {

	/**
	 * Random
	 */
	private static final long serialVersionUID = 8817715363221616696L;

	public ConversionError(String msg) {
		super(msg);
	}

}
