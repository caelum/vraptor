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

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Create a personalized implementation for {@link javax.validation.MessageInterpolator.Context}. This class
 * is need to interpolate the constraint violation message with localized messages.
 * 
 * @author Otávio Scherer Garcia
 * @version $Revision$
 */
class BeanValidatorContext
    implements MessageInterpolator.Context {

    private final ConstraintDescriptor<?> descriptor;
    private final Object validatedValue;

    private BeanValidatorContext(ConstraintDescriptor<?> descriptor, Object validatedValue) {
        this.descriptor = descriptor;
        this.validatedValue = validatedValue;
    }

    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return descriptor;
    }

    public Object getValidatedValue() {
        return validatedValue;
    }

    public static BeanValidatorContext of(ConstraintViolation<Object> violation) {
        return new BeanValidatorContext(violation.getConstraintDescriptor(), violation.getInvalidValue());
    }
}
