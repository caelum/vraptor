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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.Results;

/**
 * Intercepts the request and forwards to the default view if no view was
 * rendered so far.
 *
 * @author Guilherme Silveira
 */
@Intercepts(after=ExecuteMethodInterceptor.class, before={})
public class ForwardToDefaultViewInterceptor implements Interceptor {
    private final Result result;

    private static final Logger logger = LoggerFactory.getLogger(ForwardToDefaultViewInterceptor.class);

    public ForwardToDefaultViewInterceptor(Result result) {
        this.result = result;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        if (result.used()) {
        	logger.debug("Request already dispatched and commited somewhere else, not forwarding.");
            return;
        }

        // TODO: maybe the response.isCommited is true, we should warn before
        // trying to forward
        logger.debug("forwarding to the dafault page for this logic");
        result.use(Results.page()).defaultView();
    }

}
