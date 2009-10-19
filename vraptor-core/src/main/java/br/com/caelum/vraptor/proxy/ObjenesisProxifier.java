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
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.ObjenesisStd;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class ObjenesisProxifier implements Proxifier {

	private static final List<Method> OBJECT_METHODS = Arrays.asList(Object.class.getDeclaredMethods());

    private static final CallbackFilter IGNORE_BRIDGE_AND_OBJECT_METHODS = new CallbackFilter() {
        public int accept(Method method) {
            return method.isBridge() || OBJECT_METHODS.contains(method) ? 1 : 0;
        }
    };

	public <T> T proxify(Class<T> type, MethodInvocation<? super T> handler) {
		Class<?> proxyClass = enhanceTypeWithCGLib(type, handler).createClass();
		Factory proxyInstance = (Factory) new ObjenesisStd().newInstance(proxyClass);
		proxyInstance.setCallbacks(new Callback[] {cglibMethodInterceptor(handler), NoOp.INSTANCE});
		return type.cast(proxyInstance);
	}

	@SuppressWarnings("unchecked")
	private Enhancer enhanceTypeWithCGLib(Class type, final MethodInvocation handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallbackFilter(IGNORE_BRIDGE_AND_OBJECT_METHODS);
        enhancer.setCallbackTypes(new Class[] {MethodInterceptor.class, NoOp.class});
        return enhancer;
    }

	private MethodInterceptor cglibMethodInterceptor(final MethodInvocation handler) {
		return new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, final MethodProxy methodProxy) {
				return handler.intercept(proxy, method, args, new SuperMethod() {
				    public Object invoke(Object proxy, Object[] args) {
				        try {
				            return methodProxy.invokeSuper(proxy, args);
				        } catch (Throwable throwable) {
				            throw new ProxyInvocationException(throwable);
				        }
				    }
				});
			}
        };
	}

}
