package br.com.caelum.vraptor.view;

import java.util.List;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.validator.Message;

/**
 * Component responsible for instantiating corresponding validation form of given view.
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
public interface ValidationViewsFactory {
	/**
	 * Create an instance of the validation version of the given view.
	 */
	<T extends View> T instanceFor(Class<T> view, List<Message> errors);
}
