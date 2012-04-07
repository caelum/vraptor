/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import javax.annotation.PostConstruct;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Bring up Bean Validation factory. This class builds the {@link Validator} once when application
 * starts.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@ApplicationScoped
@Component
public class ValidatorCreator implements ComponentFactory<Validator> {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorCreator.class);

	private final ValidatorFactory factory;

	private Validator validator;

    public ValidatorCreator(ValidatorFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void createValidator() {
    	this.validator = factory.getValidator();
    	logger.debug("Initializing Bean Validator");
    }

	public Validator getInstance() {
		return validator;
	}

}