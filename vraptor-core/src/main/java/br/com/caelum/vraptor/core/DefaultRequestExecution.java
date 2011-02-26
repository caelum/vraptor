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
import br.com.caelum.vraptor.interceptor.DeserializingInterceptor;
import br.com.caelum.vraptor.interceptor.ExceptionHandlerInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * A request execution process. The default order is extremely important but
 * this behaviour can be change by providing your own RequestExecution.
 *
 * @author Guilherme Silveira
 * @deprecated This class is deprecated. If you extend a request execution, consider using @Intercepts(after=..., before=...) instead.
 */
@Deprecated
@PrototypeScoped
public class DefaultRequestExecution implements RequestExecution {

	private static final Logger logger = LoggerFactory.getLogger(DefaultRequestExecution.class);

    private final InterceptorStack interceptorStack;

    public DefaultRequestExecution(InterceptorStack interceptorStack) {
        this.interceptorStack = interceptorStack;
    }

    public void execute() throws InterceptionException {
    	logger.debug("executing stack  DefaultRequestExecution");

    	interceptorStack.add(MultipartInterceptor.class);
        interceptorStack.add(ResourceLookupInterceptor.class);
        interceptorStack.add(FlashInterceptor.class);
        interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
        interceptorStack.add(InstantiateInterceptor.class);
        interceptorStack.add(ParametersInstantiatorInterceptor.class);
        interceptorStack.add(DeserializingInterceptor.class);
        interceptorStack.add(ExceptionHandlerInterceptor.class);
        interceptorStack.add(ExecuteMethodInterceptor.class);
        interceptorStack.add(OutjectResult.class);
        interceptorStack.add(DownloadInterceptor.class);
        interceptorStack.add(ForwardToDefaultViewInterceptor.class);
        interceptorStack.next(null, null);
    }
}
