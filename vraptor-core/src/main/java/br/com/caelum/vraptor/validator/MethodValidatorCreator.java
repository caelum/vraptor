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
import javax.validation.Validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Bring up Method Validation factory. This class builds the {@link MethodValidator} factory once when
 * application starts.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5
 */
@ApplicationScoped
public class MethodValidatorCreator
    implements ComponentFactory<MethodValidator> {

    private static final Logger logger = LoggerFactory.getLogger(MethodValidatorCreator.class);

    private MethodValidator methodValidator;

    @PostConstruct
    public void init() {
        methodValidator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory()
                .getValidator().unwrap(MethodValidator.class);
        logger.debug("Initializing Method Validator");
    }

    public MethodValidator getInstance() {
        return methodValidator;
    }

}
