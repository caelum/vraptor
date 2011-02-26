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

package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.view.Results.nothing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.Stringnifier;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Interceptor that executes the logic method.
 *
 * @author Guilherme Silveira
 */
@Intercepts(after=ParametersInstantiatorInterceptor.class, before={})
public class ExecuteMethodInterceptor implements Interceptor {

	private final MethodInfo info;
	private final Validator validator;
	private final static Logger log = LoggerFactory.getLogger(ExecuteMethodInterceptor.class);

	public ExecuteMethodInterceptor(MethodInfo info, Validator validator) {
		this.info = info;
		this.validator = validator;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		try {
			Method reflectionMethod = method.getMethod();
			Object[] parameters = this.info.getParameters();

			log.debug("Invoking {}", Stringnifier.simpleNameFor(reflectionMethod));
			Object result = reflectionMethod.invoke(resourceInstance, parameters);

			if (validator.hasErrors()) { // method should have thrown ValidationException
				if (log.isDebugEnabled()) {
					try {
						validator.onErrorUse(nothing());
					} catch (ValidationException e) {
						log.debug("Some validation errors occured: {}", e.getErrors());
					}
				}
				throw new InterceptionException(
						"There are validation errors and you forgot to specify where to go. Please add in your method "
						+ "something like:\n"
						+ "validator.onErrorUse(page()).of(AnyController.class).anyMethod();\n"
						+ "or any view that you like.\n"
						+ "If you didn't add any validation error, it is possible that a conversion error had happened.");
			}

			if (reflectionMethod.getReturnType().equals(Void.TYPE)) {
				// vraptor2 compatibility
				this.info.setResult("ok");
			} else {
				this.info.setResult(result);
			}
			stack.next(method, resourceInstance);
		} catch (IllegalArgumentException e) {
			throw new InterceptionException(e);
		} catch (IllegalAccessException e) {
			throw new InterceptionException(e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ValidationException) {
				// fine... already parsed
				log.trace("swallowing {}", cause);
			} else {
				throw new InterceptionException("exception raised, check root cause for details: " + cause, cause);
			}
		}
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

}
