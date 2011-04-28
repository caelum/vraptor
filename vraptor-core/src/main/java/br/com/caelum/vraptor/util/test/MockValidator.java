/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.util.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.validator.AbstractValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
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
public class MockValidator extends AbstractValidator {

	private final List<Message> errors = new ArrayList<Message>();

	public void checking(Validations validations) {
		this.errors.addAll(validations.getErrors());
	}

	public void validate(Object object) {
	}

	public <T extends View> T onErrorUse(Class<T> view) {
		if(!this.errors.isEmpty()) {
			throw new ValidationException(errors);
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

	public List<Message> getErrors() {
		return Collections.unmodifiableList(errors);
	}
}
