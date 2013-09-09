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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Implements the {@link BeanValidator} using Bean Validation (JSR303). This implementation
 * will be enable by vraptor when any implementation of Bean Validation is available into classpath.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@RequestScoped
@Component
public class DefaultBeanValidator
    implements BeanValidator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanValidator.class);

    private final Localization localization;

    private final Validator validator;

    private final MessageInterpolator interpolator;

    public DefaultBeanValidator(Localization localization, Validator validator, MessageInterpolator interpolator) {
        this.localization = localization;
        this.validator = validator;
        this.interpolator = interpolator;
    }

    public List<Message> validate(Object bean, Class<?>... groups) {
        if (bean == null) {
            logger.warn("skiping validation, input bean is null.");
            return emptyList();
        }

        final Set<ConstraintViolation<Object>> violations = validator.validate(bean, groups);
        logger.debug("there are {} violations at bean {}.", violations.size(), bean);

        return getMessages(violations);
    }

    public List<Message> validateProperties(Object bean, String... properties) {
    	if(bean == null) {
    		logger.warn("skiping validation, input bean is null.");
            return emptyList();
    	}
    	
    	checkArgument(hasProperties(properties), "No properties were defined to be validated");
    	
    	List<Message> messages = new ArrayList<Message>();
    	
    	for(String property : properties) {        
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(bean, property);
            logger.debug("there are {} violations at bean {}.", violations.size(), bean);

            messages.addAll(getMessages(violations));
        }
    	
    	return messages;
    }
    
    public List<Message> validateProperty(Object bean, String property, Class<?>... groups) {
    	if(bean == null) {
            logger.warn("skiping validation, input bean is null.");
            return emptyList();
    	}
    	
    	List<Message> messages = new ArrayList<Message>();
    	
    	Set<ConstraintViolation<Object>> violations = validator.validateProperty(bean, property, groups);
    	logger.debug("there are {} violations at bean {}.", violations.size(), bean);

    	messages.addAll(getMessages(violations));
    	
    	return messages;
    }
    
    private List<Message> getMessages(final Set<ConstraintViolation<Object>> violations) {
    	List<Message> messages = new ArrayList<Message>();
    	
    	for(ConstraintViolation<Object> violation : violations) {
    		BeanValidatorContext ctx = BeanValidatorContext.of(violation);
    		String msg = interpolator.interpolate(violation.getMessageTemplate(), ctx, getLocale());
    		
    		messages.add(new ValidationMessage(msg, violation.getPropertyPath().toString()));
    		logger.debug("added message {} to validation of bean {}", msg, violation.getRootBean());
    	}
    	
    	return messages;
    }

    private Locale getLocale() {
    	return localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();
    }

    private boolean hasProperties(String... properties) {
    	return properties != null && properties.length > 0;
    }

}
