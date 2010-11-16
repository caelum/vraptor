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

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Default implementation for ValidationViewsFactory
 *
 * If you want to extend this behavior use:
 * @Component
 * public class MyValidatorViewsFactory extends DefaultValidationViewsFactory {
 * 		//delegate constructor
 * 		@Override
 * 		public <T extends View> T instanceFor(Class<T> view, List<Message> errors) {
 * 			//return my own Validation view version or
 * 			return super.instanceFor(view, errors);
 * 		}
 * }
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
public class DefaultValidationViewsFactory implements ValidationViewsFactory {

	private final Result result;
	private final Proxifier proxifier;

	public DefaultValidationViewsFactory(Result result, Proxifier proxifier) {
		this.result = result;
		this.proxifier = proxifier;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T instanceFor(final Class<T> view, final List<Message> errors) {
		if (view.equals(EmptyResult.class)) {
			throw new ValidationException(errors);
		}

		final T viewInstance = result.use(view);

		return proxifier.proxify(view, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				final Object instance = new Mirror().on(viewInstance).invoke().method(method).withArgs(args);
				if (method.getReturnType() == void.class) {
					throw new ValidationException(errors);
				}

				if (view.isAssignableFrom(method.getReturnType())) {
					return proxy;
				}

				if (args.length > 0 && args[0] instanceof Class) {
					return proxifier.proxify((Class) args[0], new MethodInvocation() {
						public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
							new Mirror().on(instance).invoke().method(method).withArgs(args);

							throw new ValidationException(errors);
						}
					});
				}
				throw new ResultException("It's not possible to create a validation version of " + method + ". You must provide a Custom Validation version of your class, or inform this corner case to VRaptor developers");
			}
		});

	}
}
