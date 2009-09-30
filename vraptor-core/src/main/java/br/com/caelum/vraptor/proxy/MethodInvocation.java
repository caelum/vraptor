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
package br.com.caelum.vraptor.proxy;

import java.lang.reflect.Method;

/**
 * Callback for method invocations on proxies.
 *
 * @author Fabio Kung
 * @param <T> type of the proxy that will have its method calls intercepted
 */
public interface MethodInvocation<T> {

    /**
     * Executes for all method invocations on proxies.
     *
     * @param proxy       that received the method call.
     * @param method      called.
     * @param args        given to the method call.
     * @param superMethod allowing calls to the overriden original method. A super method call only makes sense for
     *                    concrete class proxies.
     * @return the method call return.
     */
    Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod);
}
