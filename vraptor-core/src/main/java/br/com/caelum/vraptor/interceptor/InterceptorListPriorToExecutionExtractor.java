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

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.collect.Iterables;

/**
 * Extracts all interceptors which are supposed to be applied for this current
 * resource method.
 *
 * @author Guilherme Silveira
 */
public class InterceptorListPriorToExecutionExtractor implements Interceptor {

    private final InterceptorRegistry registry;

    public InterceptorListPriorToExecutionExtractor(InterceptorRegistry registry) {
        this.registry = registry;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
    	for (Class<? extends Interceptor> type : Iterables.reverse(registry.all())) {
			stack.addAsNext(type);
		}
        stack.next(method, resourceInstance);
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
