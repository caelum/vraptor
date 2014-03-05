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
package br.com.caelum.vraptor.core;

import java.io.IOException;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.LateResponseCommitHandler;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * Delegates ordering to {@link InterceptorRegistry} 
 * @author Lucas Cavalcanti
 * @since 3.3.0
 *
 */
@PrototypeScoped
public class EnhancedRequestExecution implements RequestExecution {

	private final InterceptorRegistry registry;
	private final InterceptorStack stack;
	private final LateResponseCommitHandler responseHandler;

	public EnhancedRequestExecution(InterceptorRegistry registry, InterceptorStack stack,
			LateResponseCommitHandler responseHandler) {
		this.registry = registry;
		this.stack = stack;
		this.responseHandler = responseHandler;
	}


	public void execute() throws VRaptorException {
		for (Class<? extends Interceptor> interceptor : registry.all()) {
			stack.add(interceptor);
		}
		stack.next(null, null);
		try {
			responseHandler.commit();
		} catch (IOException e) {
			throw new VRaptorException("IO error when commiting the response", e);
		}
	}

}
