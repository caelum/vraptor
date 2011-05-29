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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Cglib implementation for {@link Proxifier}.
 * 
 * @author Fábio Kung
 * @autor Guilherme Silveira
 * @since 3.3.1, refactored from older version
 */
@ApplicationScoped
public class CglibProxifier
    implements Proxifier {

    private static final Logger logger = LoggerFactory.getLogger(CglibProxifier.class);

    /**
     * Methods like toString and finalize will be ignored.
     */
    private static final List<Method> OBJECT_METHODS = Arrays.asList(Object.class.getDeclaredMethods());

    /**
     * Do not proxy these methods.
     */
    private static final CallbackFilter IGNORE_BRIDGE_AND_OBJECT_METHODS = new CallbackFilter() {
        public int accept(Method method) {
            return method.isBridge() || OBJECT_METHODS.contains(method) ? 1 : 0;
        }
    };

    private final InstanceCreator instanceCreator;

    public CglibProxifier(InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    public <T> T proxify(Class<T> type, MethodInvocation<? super T> handler) {
        Class<?> proxyClass = enhanceTypeWithCGLib(type, handler).createClass();
        Factory proxyInstance = (Factory) instanceCreator.instanceFor(proxyClass);
        proxyInstance.setCallbacks(new Callback[] { cglibMethodInterceptor(handler), NoOp.INSTANCE });

        logger.debug("a proxy for {} is created as {}", type, proxyClass);

        return type.cast(proxyInstance);
    }

    public boolean isProxy(Object o) {
        return o != null && Factory.class.isAssignableFrom(o.getClass());
    }

    protected <T> Enhancer enhanceTypeWithCGLib(Class<T> type, final MethodInvocation<? super T> handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallbackFilter(IGNORE_BRIDGE_AND_OBJECT_METHODS);
        enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });

        return enhancer;
    }

    protected <T> MethodInterceptor cglibMethodInterceptor(final MethodInvocation<? super T> handler) {
        return new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, final MethodProxy methodProxy) {
                return handler.intercept((T) proxy, method, args, new SuperMethod() {
                    public Object invoke(Object proxy, Object[] args) {
                        try {
                            logger.debug("proxy invoke, proxy {}, method {}", proxy, methodProxy);

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
