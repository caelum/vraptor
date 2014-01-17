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

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;

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
 * Validate method parameters using Bean Validation 1.1. The method will be intercepted if any parameter
 * contains Bean Validation annotations. This component is enabled only if you have any Bean Validation
 * provider that implements method validation.
 *
 * @author Otávio Scherer Garcia
 * @since 3.5.2
 */
@RequestScoped
@Intercepts(before = ExecuteMethodInterceptor.class, after = ParametersInstantiatorInterceptor.class)
public class MethodValidatorInterceptor
	implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(MethodValidatorInterceptor.class);

	private final javax.validation.Validator methodValidator;
	private final Localization localization;
	private final MessageInterpolator interpolator;
	private final MethodInfo methodInfo;
	private final Validator validator;
	private final ParameterNameProvider parameterNameProvider;

	public MethodValidatorInterceptor(Localization localization, MessageInterpolator interpolator, Validator validator,
		MethodInfo methodInfo, javax.validation.Validator methodValidator, ParameterNameProvider parameterNameProvider) {
	this.localization = localization;
	this.interpolator = interpolator;
	this.validator = validator;
	this.methodInfo = methodInfo;
	this.methodValidator = methodValidator;
		this.parameterNameProvider = parameterNameProvider;
	}

	public boolean accepts(ResourceMethod method) {
	BeanDescriptor bean = methodValidator.getConstraintsForClass(method.getResource().getType());
	MethodDescriptor descriptor = bean.getConstraintsForMethod(method.getMethod().getName(), method.getMethod().getParameterTypes());
	return descriptor != null && descriptor.hasConstrainedParameters();
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
	throws InterceptionException {

	String[] paramNames = parameterNameProvider.parameterNamesFor(method.getMethod());
	Set<ConstraintViolation<Object>> violations = methodValidator.forExecutables().validateParameters(resourceInstance,
		method.getMethod(), methodInfo.getParameters());
	logger.debug("there are {} violations at method {}.", violations.size(), method);

	for (ConstraintViolation<Object> violation : violations) {
		BeanValidatorContext ctx = BeanValidatorContext.of(violation);
		String msg = interpolator.interpolate(violation.getMessageTemplate(), ctx, getLocale());

		validator.add(new ValidationMessage(msg, extractCategory(paramNames, violation)));
		logger.debug("added message {} to validation of bean {}", msg, violation.getRootBean());
	}

	stack.next(method, resourceInstance);
	}

	private Locale getLocale() {
	return localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();
	}

	/**
	 * Returns the category for this constraint violation. By default, the category returned
	 * is the name of method with full path for property. You can override this method to
	 * change this behaviour.
	 */
	protected String extractCategory(String[] paramNames, ConstraintViolation<Object> violation) {
		Iterator<Node> path = violation.getPropertyPath().iterator();

		StringBuilder cat = new StringBuilder();
		cat.append(path.next()).append("."); // method name
		cat.append(paramNames[path.next().as(ParameterNode.class).getParameterIndex()]);// parameter name

		while (path.hasNext()) {
			cat.append(".").append(path.next());
		}

		return cat.toString();
	}
}
