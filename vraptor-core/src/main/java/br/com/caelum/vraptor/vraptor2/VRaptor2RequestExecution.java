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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * A vraptor 2 request execution process.
 *
 * @author Guilherme Silveira
 */
@Component
@PrototypeScoped
public class VRaptor2RequestExecution implements RequestExecution {

	private static final Logger logger = LoggerFactory.getLogger(VRaptor2RequestExecution.class);

	private final InterceptorStack interceptorStack;
	private final boolean shouldRegisterHibernateValidator;

	public VRaptor2RequestExecution(InterceptorStack interceptorStack,
			Config config) {
		this.interceptorStack = interceptorStack;
		this.shouldRegisterHibernateValidator = config
				.hasPlugin("org.vraptor.plugin.hibernate.HibernateValidatorPlugin");
	}

	public void execute() throws InterceptionException {
		logger.debug("executing stack  VRaptor2RequestExecution");

		interceptorStack.add(ResourceLookupInterceptor.class);
		interceptorStack.add(FlashInterceptor.class);
		interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
		interceptorStack.add(DownloadInterceptor.class);
		interceptorStack.add(MultipartInterceptor.class);
		interceptorStack.add(InstantiateInterceptor.class);
		interceptorStack.add(ParametersInstantiatorInterceptor.class);
		if (shouldRegisterHibernateValidator) {
			interceptorStack.add(HibernateValidatorPluginInterceptor.class);
		}
		interceptorStack.add(ValidatorInterceptor.class);
		interceptorStack.add(ExecuteMethodInterceptor.class);
		interceptorStack.add(OutjectResult.class);
		interceptorStack.add(OutjectionInterceptor.class);
		interceptorStack.add(AjaxInterceptor.class);
		interceptorStack.add(ViewInterceptor.class);
		interceptorStack.next(null, null);
	}
}
