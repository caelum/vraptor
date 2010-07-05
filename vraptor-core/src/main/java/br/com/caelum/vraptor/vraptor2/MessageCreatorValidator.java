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

package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vraptor.i18n.FixedMessage;
import org.vraptor.i18n.ValidationMessage;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.validator.AbstractValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

/**
 * The vraptor2 compatible messages creator.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class MessageCreatorValidator extends AbstractValidator {

	private final Result result;
	private final ValidationErrors errors;

	private final ResourceMethod resource;
	private final MethodInfo info;
	private boolean containsErrors;
	private final ValidationViewsFactory viewsFactory;

	public MessageCreatorValidator(Result result, ValidationErrors errors, MethodInfo info,
			ValidationViewsFactory viewsFactory) {
		this.result = result;
		this.errors = errors;
		this.viewsFactory = viewsFactory;
		this.resource = info.getResourceMethod();
		this.info = info;
	}

	public void checking(Validations validations) {
		List<Message> messages = validations.getErrors();
		for (Message s : messages) {
			add(s);
		}
	}

	public void validate(Object object) {
	     throw new UnsupportedOperationException("this feature is not supported by vraptor2");
	}

	public <T extends View> T onErrorUse(Class<T> view) {
		if (!hasErrors()) {
			return new MockResult().use(view); // ignore anything, no errors
												// occurred
		}
		result.include("errors", errors);
		if (Info.isOldComponent(resource.getResource())) {
			info.setResult("invalid");
			result.use(Results.page()).forward();
			throw new ValidationException(new ArrayList<Message>());
		} else {
			return viewsFactory.instanceFor(view, new ArrayList<Message>());
		}
	}

	public void add(Message message) {
		containsErrors = true;
		this.errors.add(new FixedMessage(message.getCategory(), message.getMessage(), message.getCategory()));
	}

	public void addAll(Collection<? extends Message> messages) {
		for (Message message : messages) {
			this.add(message);
		}
	}

	public boolean hasErrors() {
		return containsErrors;
	}

	@Override
	protected List<Message> getErrors() {
		List<Message> messages = new ArrayList<Message>();
		for (ValidationMessage message : errors) {
			messages.add(new br.com.caelum.vraptor.validator.ValidationMessage(message.getPath(), message.getCategory()));
		}
		return messages;
	}
}
