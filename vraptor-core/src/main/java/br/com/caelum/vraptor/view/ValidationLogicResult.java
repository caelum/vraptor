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
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Validation implementation for Logic Result
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
public class ValidationLogicResult implements LogicResult {

	private final Proxifier proxifier;
	private final LogicResult delegate;
	private final List<Message> errors;

	public ValidationLogicResult(LogicResult delegate, Proxifier proxifier, List<Message> errors) {
		this.delegate = delegate;
		this.proxifier = proxifier;
		this.errors = errors;
	}
	public <T> T forwardTo(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				try {
					method.invoke(delegate.forwardTo(type), args);
				} catch (Exception e) {
					throw new ResultException(e);
				}
				throw new ValidationError(errors);
			}
		});
	}

	public <T> T redirectTo(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				try {
					method.invoke(delegate.redirectTo(type), args);
				} catch (Exception e) {
					throw new ResultException(e);
				}
				throw new ValidationError(errors);
			}
		});
	}

}
