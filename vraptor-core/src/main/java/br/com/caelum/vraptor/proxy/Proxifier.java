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

/**
 * Implementations of this interface are used to create Proxy instances whenever needed.
 * 
 * @author Fabio Kung
 */
public interface Proxifier {

    /**
     * Creates a proxy for class defined in type parameter.
     * 
     * @param type The proxy type.
     * @param handler Callback for method invocation.
     * @return The proxy.
     */
    <T> T proxify(Class<T> type, MethodInvocation<? super T> handler);

    /**
     * Return <code>true</code> if the object is a proxy, false otherwise. <code>null</code> objects always return
     * <code>false</code>.
     * 
     * @param o The object to test
     * @return <code>true</code> if the object is a proxy, false otherwise.
     */
    boolean isProxy(Object o);
}
