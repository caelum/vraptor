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

package br.com.caelum.vraptor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A handler based in an already instatiated interceptor. Whenever this handler
 * is invoked, the underlying interceptor is invoked.
 *
 * @author Guilherme Silveira
 */
public class InstantiatedInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = LoggerFactory.getLogger(InstantiatedInterceptorHandler.class);

	private final Interceptor interceptor;

	public InstantiatedInterceptorHandler(Interceptor interceptor) {
		this.interceptor = interceptor;
	}

	public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		if (interceptor.accepts(method)) {
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			interceptor.intercept(stack, method, resourceInstance);
		} else {
			stack.next(method, resourceInstance);
		}
	}

}
