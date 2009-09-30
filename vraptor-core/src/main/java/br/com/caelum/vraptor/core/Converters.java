
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.Container;

/**
 * Represents a collection of converters.<br>
 * Note: This interface will probably change in the near future to allow
 * annotation support.
 * 
 * @author Guilherme Silveira
 */
public interface Converters {

	/**
	 * Extracts a converter for this specific type.
	 * 
	 * @param type
	 * @param container
	 * @return
	 */
	Converter<?> to(Class<?> type, Container container);

	void register(Class<? extends Converter<?>> converterClass);

}
