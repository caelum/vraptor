
package br.com.caelum.vraptor.util.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;
import br.com.caelum.vraptor.validator.Validations;

/**
 * Mocked Validator for testing your controllers.
 *
 * You can use the idiom:
 * MockValidator validator = new MockValidator();
 * MyController controller = new MyController(..., validator);
 *
 * try {
 * 		controller.method();
 * 		Assert.fail();
 * } catch (ValidationError e) {
 * 		List<Message> errors = e.getErrors();
 * 		// asserts
 * }
 *
 * or
 *
 * @Test(expected=ValidationError.class)
 *
 * @author Lucas Cavalcanti
 *
 */
public class MockValidator implements Validator {

	private final List<Message> errors = new ArrayList<Message>();

	public void checking(Validations validations) {
		this.errors.addAll(validations.getErrors());
	}

	public <T extends View> T onErrorUse(Class<T> view) {
		if(!this.errors.isEmpty()) {
			throw new ValidationError(errors);
		}
		return new MockResult().use(view);
	}

	public void addAll(Collection<? extends Message> messages) {
		this.errors.addAll(messages);
	}

	public void add(Message message) {
		this.errors.add(message);
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

}
