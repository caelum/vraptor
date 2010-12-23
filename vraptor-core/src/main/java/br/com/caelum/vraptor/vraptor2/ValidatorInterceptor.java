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

package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * Interceptor which invokes vraptor2-like validation methods.
 * @author guilherme silveira
 *
 */
@Component
public class ValidatorInterceptor implements Interceptor {

    private final ParametersProvider provider;
    private final Result result;

    private static final Logger logger = LoggerFactory.getLogger(ValidatorInterceptor.class);
    private final ValidationErrors errors;
    private final OutjectionInterceptor outjection;
    private final Localization localization;
	private final MethodInfo info;

    public ValidatorInterceptor(ParametersProvider provider, Result result, ValidationErrors errors, OutjectionInterceptor outjection, Localization localization, MethodInfo info) {
        this.provider = provider;
        this.result = result;
        this.errors = errors;
        this.outjection = outjection;
        this.localization = localization;
		this.info = info;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (Info.isOldComponent(method.getResource())) {
            invokeValidationMethod(method, resourceInstance);
            if (errors.size() != 0) {
                this.outjection.outject(resourceInstance, method.getResource().getType());
                this.result.include("errors", errors);
                this.info.setResult("invalid");
                this.result.use(Results.page()).defaultView();
                return;
            }
        }
        stack.next(method, resourceInstance);
    }

	private void invokeValidationMethod(ResourceMethod method, Object resourceInstance) {
		Method validationMethod = getValidationFor(method);
		if (validationMethod == null) {
			return;
		}
		List<Message> convertionErrors = new ArrayList<Message>();
		Object[] parameters = provider.getParametersFor(method, convertionErrors, localization.getBundle());
		BasicValidationErrors newErrors = new BasicValidationErrors();
		Object[] validationParameters = createValidatonParameters(newErrors, parameters);
		new Mirror().on(resourceInstance).invoke().method(validationMethod).withArgs(validationParameters);
		addConvertionErrors(convertionErrors);
		convertErrors(newErrors);
	}

	private Object[] createValidatonParameters(BasicValidationErrors newErrors, Object[] parameters) {
		Object[] validationParameters = new Object[parameters.length + 1];
		validationParameters[0] = newErrors;
		System.arraycopy(parameters, 0, validationParameters, 1, parameters.length);
		return validationParameters;
	}

	private void addConvertionErrors(List<Message> convertionErrors) {
		for (Message msg : convertionErrors) {
		    errors.add(new FixedMessage(msg.getCategory(), msg.getMessage(), msg.getCategory()));
		}
	}

	private void convertErrors(BasicValidationErrors newErrors) {
		for (org.vraptor.i18n.ValidationMessage msg : newErrors) {
		    if (msg instanceof FixedMessage) {
		        errors.add(msg);
		    } else if (msg instanceof org.vraptor.i18n.Message) {
		        org.vraptor.i18n.Message m = (org.vraptor.i18n.Message) msg;
		        String content = localization.getMessage(m.getKey(), (Object[]) m.getParameters());
		        errors.add(new FixedMessage(msg.getPath(), content, msg.getCategory()));
		    } else {
		        throw new IllegalArgumentException("Unsupported validation message type: " + msg.getClass().getName());
		    }
		}
	}

    private <T> Method getValidationFor(ResourceMethod resourceMethod) {
    	Class<?> type = resourceMethod.getResource().getType();
    	Method method = resourceMethod.getMethod();
        String validationMethodName = "validate" + capitalize(method.getName());
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(validationMethodName)) {
                if (m.getParameterTypes().length != method.getParameterTypes().length + 1) {
                    logger.error("Validate method for " + method + " has a different number of args+1!");
                }
                return m;
            }
        }
        return null;
    }

    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
