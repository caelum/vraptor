package br.com.caelum.vraptor.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller used to test Generic Controllers on LinkToHandler
 * @author Nykolas Lima
 *
 */
public class SubGenericController extends GenericController<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubGenericController.class);

	public void method(String string) {
		LOGGER.debug("Do something thing by Sub Generic Controller");
	}
	
	public void okMethod(String string) {
		LOGGER.debug("OKOK");
	}
	
	public void anotherMethod(String string, String param) {
		LOGGER.debug("Do another thing by Sub Generic Controller");
	}
}
