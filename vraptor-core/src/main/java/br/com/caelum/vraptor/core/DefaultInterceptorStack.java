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

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default implmentation of a interceptor stack.
 * @author guilherme silveira
 *
 */
@RequestScoped
public class DefaultInterceptorStack implements InterceptorStack {

    private final List<InterceptorHandler> interceptors = new ArrayList<InterceptorHandler>();
    private final Container container;

    public DefaultInterceptorStack(Container container) {
        this.container = container;
    }

    /**
     * we do not use an iterator so an interceptor can hack the code to add new
     * interceptors on the fly
     */
    private int nextInterceptor = 0;

    public void add(Interceptor interceptor) {
        this.interceptors.add(new InstantiatedInterceptorHandler(interceptor));
    }

    public void next(ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (nextInterceptor == interceptors.size()) {
            return;
        }
        InterceptorHandler handler = interceptors.get(nextInterceptor++);
        handler.execute(this, method, resourceInstance);
    }

    public <T extends Interceptor> void add(Class<T> type) {
        this.interceptors.add(new ToInstantiateInterceptorHandler(container, type));
    }

    public void addAsNext(Interceptor interceptor) {
        this.interceptors.add(nextInterceptor, new InstantiatedInterceptorHandler(interceptor));
    }

}
