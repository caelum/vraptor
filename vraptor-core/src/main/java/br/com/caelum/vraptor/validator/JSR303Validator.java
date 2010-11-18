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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Implements the {@link BeanValidator} using the JSR303 - BeanValidation. This implementation will be enable by vraptor
 * when the hibernate validator classes is locale in classpath.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@RequestScoped
@Component
public class JSR303Validator
    implements BeanValidator {

    private static final Logger logger = LoggerFactory.getLogger(JSR303Validator.class);

    private final Localization localization;

	private final Validator validator;

	private final MessageInterpolator interpolator;

    public JSR303Validator(Localization localization, Validator validator, MessageInterpolator interpolator) {
        this.localization = localization;
		this.validator = validator;
		this.interpolator = interpolator;
    }

    public List<Message> validate(Object bean) {
        if (bean == null) {
            logger.warn("skiping validation, input bean is null.");
            return Collections.emptyList(); // skip if the bean is null
        }

        final Set<ConstraintViolation<Object>> violations = validator.validate(bean);
        logger.debug("there are {} violations at bean {}.", violations.size(), bean);

        Locale locale = localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();

        List<Message> messages = new ArrayList<Message>();
        for (ConstraintViolation<Object> violation : violations) {
            // interpolate the message
            final Context ctx = new Context(violation.getConstraintDescriptor(), violation.getInvalidValue());
            String msg = interpolator.interpolate(violation.getMessageTemplate(), ctx, locale);

            messages.add(new ValidationMessage(msg, violation.getPropertyPath().toString()));
            logger.debug("added message {} to validation of bean {}", msg, violation.getRootBean());
        }

        return messages;
    }

    /**
     * Create a personalized implementation for {@link javax.validation.MessageInterpolator.Context}. This class is need
     * to interpolate the constraint violation message with localized messages.
     *
     * @author Otávio Scherer Garcia
     * @version $Revision$
     */
    class Context
        implements MessageInterpolator.Context {

        private final ConstraintDescriptor<?> descriptor;
        private final Object validatedValue;

        public Context(ConstraintDescriptor<?> descriptor, Object validatedValue) {
            this.descriptor = descriptor;
            this.validatedValue = validatedValue;
        }

        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return descriptor;
        }

        public Object getValidatedValue() {
            return validatedValue;
        }
    }
}