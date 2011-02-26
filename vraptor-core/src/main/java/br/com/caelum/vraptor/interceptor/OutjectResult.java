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

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Outjects the result of the method invocation to the desired result
 *
 * @author guilherme silveira
 */
@Intercepts(after=ExecuteMethodInterceptor.class, before=ForwardToDefaultViewInterceptor.class)
@Lazy
public class OutjectResult implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(OutjectResult.class);

	private final Result result;
	private final MethodInfo info;
	private final TypeNameExtractor extractor;

	public OutjectResult(Result result, MethodInfo info, TypeNameExtractor extractor) {
		this.result = result;
		this.info = info;
		this.extractor = extractor;
	}

	public boolean accepts(ResourceMethod method) {
		Type returnType = method.getMethod().getGenericReturnType();
		return !returnType.equals(void.class);
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		Type returnType = method.getMethod().getGenericReturnType();
		String name = extractor.nameFor(returnType);
		Object value = this.info.getResult();

		logger.debug("outjecting {}={}", name, value);
		result.include(name, value);
		stack.next(method, resourceInstance);
	}

}
