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

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bring up JSR303 Bean Validation factory. This class builds the default validator factory once when application
 * starts. In the future this components needs to be application scoped (see issue 213).
 * 
 * @author Otávio Scherer Garcia
 * @since vraptor3.1.2
 */
public class JSR303ValidatorFactory {

    private static final Logger logger = LoggerFactory.getLogger(JSR303ValidatorFactory.class);

    private final Validator validator;
    private final MessageInterpolator interpolator;

    public JSR303ValidatorFactory() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.interpolator = factory.getMessageInterpolator();

        logger.debug("Initializing JSR303 factory for bean validation");
    }

    public Validator getValidator() {
        return validator;
    }

    public MessageInterpolator getInterpolator() {
        return interpolator;
    }

}