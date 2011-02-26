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

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default implementation of a interceptor stack.
 * @author guilherme silveira
 *
 */
@PrototypeScoped
public class DefaultInterceptorStack implements InterceptorStack {

	private static final Logger logger = LoggerFactory.getLogger(DefaultInterceptorStack.class);

    private final LinkedList<InterceptorHandler> interceptors = new LinkedList<InterceptorHandler>();
    private final InterceptorHandlerFactory handlerFactory;

    public DefaultInterceptorStack(InterceptorHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public void next(ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (interceptors.isEmpty()) {
        	logger.debug("All registered interceptors have been called. End of VRaptor Request Execution.");
            return;
        }
        InterceptorHandler handler = interceptors.poll();
        handler.execute(this, method, resourceInstance);
    }

    public void add(Class<? extends Interceptor> type) {
        this.interceptors.addLast(handlerFactory.handlerFor(type));
    }

    //XXX this method will be removed soon
    public void addAsNext(Class<? extends Interceptor> type) {
    	if (!type.getPackage().getName().startsWith("br.com.caelum.vraptor.interceptor") &&
    			!type.equals(ForwardToDefaultViewInterceptor.class)) {
			this.interceptors.addFirst(handlerFactory.handlerFor(type));
		}
    }

    @Override
    public String toString() {
    	return "DefaultInterceptorStack " + interceptors;
    }

}
