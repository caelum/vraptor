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
import java.util.List;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Uses hibernate's validator api to check for error messages.
 * 
 * @author Guilherme Silveira
 * @deprecated Use the {@link BeanValidator}.
 */
@Deprecated
public class Hibernate {

    private static final ValidatorLocator locator = new ValidatorLocator();

    @SuppressWarnings("unchecked")
    public static List<Message> validate(Object instance) {
        List<Message> errors = new ArrayList<Message>();
        ClassValidator<Object> validator = (ClassValidator<Object>) locator.getValidator(instance.getClass(), null);
        InvalidValue[] invalidValues = validator.getInvalidValues(instance);

        for (InvalidValue value : invalidValues) {
            errors.add(new ValidationMessage(value.getMessage(), value.getPropertyPath()));
        }
        return errors;
    }

}
