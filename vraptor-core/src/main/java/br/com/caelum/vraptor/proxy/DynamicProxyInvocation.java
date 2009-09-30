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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Kung
 */
class DynamicProxyInvocation implements InvocationHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicProxyInvocation.class);

    private final MethodInvocation handler;

    public DynamicProxyInvocation(MethodInvocation handler) {
        this.handler = handler;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        return handler.intercept(proxy, method, args, new SuperMethod() {
            public Object invoke(Object proxy, Object[] args) {
                LOGGER.warn("Trying to invoke the original method for an interface Proxy. You should be checking if " +
                        "the proxy is for an interface first. Better to avoid doing this.");
                return null;
            }
        });
    }
}
