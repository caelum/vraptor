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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.MessageInterpolator;
import javax.validation.Valid;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Validate method parameters using Hibernate Validator. The method will be intercepted if any parameter
 * contains Bean Validation annotations. This component is enabled only if Hibernate Validator with method
 * validation support is available.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5
 */
@RequestScoped
@Intercepts(before = ExecuteMethodInterceptor.class, after = ParametersInstantiatorInterceptor.class)
public class MethodValidatorInterceptor
    implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodValidatorInterceptor.class);

    private final MethodValidator methodValidator;
    private final ParameterNameProvider provider;
    private final Localization localization;
    private final MessageInterpolator interpolator;
    private final MethodInfo methodInfo;
    private final Validator validator;

    public MethodValidatorInterceptor(ParameterNameProvider provider, Localization localization,
            MessageInterpolator interpolator, Validator validator, MethodInfo methodInfo,
            MethodValidator methodValidator) {
        this.provider = provider;
        this.localization = localization;
        this.interpolator = interpolator;
        this.validator = validator;
        this.methodInfo = methodInfo;
        this.methodValidator = methodValidator;
    }

    public boolean accepts(ResourceMethod method) {
        return hasConstraint(method.getMethod());
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {

        Set<MethodConstraintViolation<Object>> violations = methodValidator.validateAllParameters(resourceInstance,
                method.getMethod(), methodInfo.getParameters());
        logger.debug("there are {} violations at method {}.", violations.size(), method);

        String[] paramNames = provider.parameterNamesFor(method.getMethod());
        Locale locale = localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();

        for (MethodConstraintViolation<Object> violation : violations) {
            BeanValidatorContext ctx = BeanValidatorContext.of(violation);
            String msg = interpolator.interpolate(violation.getMessageTemplate(), ctx, locale);

            String prefix = paramNames[violation.getParameterIndex()];
            String path = violation.getPropertyPath().toString();

            validator.add(new ValidationMessage(msg, prefix + path.substring(path.lastIndexOf(")") + 1)));
            logger.debug("added message {} to validation of bean {}", msg, violation.getRootBean());
        }

        stack.next(method, resourceInstance);
    }

    /**
     * Return true if any bean validation constraint is found in method parameters, false otherwise.
     * 
     * @param m The method that we need to know about annotations.
     * @return
     */
    private boolean hasConstraint(Method m) {
        for (Annotation[] annotations : m.getParameterAnnotations()) {
            for (Annotation a : annotations) {
                if (a.annotationType().isAnnotationPresent(Constraint.class) || a instanceof Valid) {
                    return true;
                }
            }
        }
        return false;
    }

}
