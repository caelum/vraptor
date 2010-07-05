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

package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

/**
 * The default validator implementation.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultValidator.class);

    private final Result result;

	private final List<Message> errors = new ArrayList<Message>();
	private final ValidationViewsFactory viewsFactory;
	private final List<BeanValidator> beanValidators; //registered bean-validators

	private final Outjector outjector;

	private final Proxifier proxifier;

	private final Localization localization;

    public DefaultValidator(Result result, ValidationViewsFactory factory, Outjector outjector, Proxifier proxifier, List<BeanValidator> beanValidators, Localization localization) {
        this.result = result;
		this.viewsFactory = factory;
		this.outjector = outjector;
		this.proxifier = proxifier;
		this.beanValidators = beanValidators;
		this.localization = localization;
    }

    public void checking(Validations validations) {
        addAll(validations.getErrors(localization.getBundle()));
    }

    public void validate(Object object) {
        if (beanValidators == null || beanValidators.isEmpty()) {
            logger.warn("has no validators registered");
        } else {
            for (BeanValidator validator : beanValidators) {
                addAll(validator.validate(object));
            }
        }
    }

    public <T extends View> T onErrorUse(Class<T> view) {
    	if (!hasErrors()) {
    		return new MockResult(proxifier).use(view); //ignore anything, no errors occurred
    	}
    	result.include("errors", errors);
    	outjector.outjectRequestMap();
    	return viewsFactory.instanceFor(view, errors);
    }

    public void addAll(Collection<? extends Message> message) {
		this.errors.addAll(message);
	}

    public void add(Message message) {
    	this.errors.add(message);
    }

	public boolean hasErrors() {
		return !errors.isEmpty();
	}
}
