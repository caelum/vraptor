package br.com.caelum.vraptor.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller used to test Generic Controllers on LinkToHandler
 * @author Nykolas Lima
 *
 */
public class GenericController<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericController.class);
	
	public void method(T entity) {
		LOGGER.debug("Do Something");
	}
	
	public void anotherMethod(T entity, String param) {
		LOGGER.debug("Do Another Thing");
	}
	
	public void methodWithoutGenericType(String param) {
		LOGGER.debug("Without generic");
	}
}
