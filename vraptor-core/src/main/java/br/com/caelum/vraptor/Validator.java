
package br.com.caelum.vraptor;

import java.util.Collection;

import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Validations;

/**
 * A validator interface for vraptor3.<br>
 * Based on hamcrest, it allows you to assert for specific situations.
 *
 * @author Guilherme Silveira
 */
public interface Validator {

    void checking(Validations rules);

	<T extends View> T onErrorUse(Class<T> view);

	void addAll(Collection<? extends Message> message);

	void add(Message message);

	boolean hasErrors();

}
