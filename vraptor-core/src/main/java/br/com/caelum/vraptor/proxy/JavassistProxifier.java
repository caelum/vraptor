/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Javassist implementation for {@link Proxifier}.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.3.1
 */
@ApplicationScoped
public class JavassistProxifier
    implements Proxifier {

    private static final Logger logger = LoggerFactory.getLogger(JavassistProxifier.class);

    /**
     * Methods like toString and finalize will be ignored.
     */
    private static final List<Method> OBJECT_METHODS = Arrays.asList(Object.class.getDeclaredMethods());

    /**
     * Do not proxy these methods.
     */
    private static final MethodFilter IGNORE_BRIDGE_AND_OBJECT_METHODS = new MethodFilter() {
        public boolean isHandled(Method method) {
            return !method.isBridge() && !OBJECT_METHODS.contains(method);
        }
    };

    private final InstanceCreator instanceCreator;

    public JavassistProxifier(InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    public <T> T proxify(Class<T> type, MethodInvocation<? super T> handler) {
        final ProxyFactory factory = new ProxyFactory();
        factory.setFilter(IGNORE_BRIDGE_AND_OBJECT_METHODS);

        if (type.isInterface()) {
            factory.setInterfaces(new Class[] { type });
        } else {
            factory.setSuperclass(type);
        }

        Class<?> proxyClass = factory.createClass();

        Object proxyInstance = instanceCreator.instanceFor(proxyClass);
        setHandler(proxyInstance, handler);

        logger.debug("a proxy for {} is created as {}", type, proxyClass);

        return type.cast(proxyInstance);
    }

    public boolean isProxy(Object o) {
        return o != null && ProxyObject.class.isAssignableFrom(o.getClass());
    }
    
    private <T> void setHandler(Object proxyInstance, final MethodInvocation<? super T> handler) {
        ProxyObject proxyObject = (ProxyObject) proxyInstance;

        proxyObject.setHandler(new MethodHandler() {
            public Object invoke(final Object self, final Method thisMethod, final Method proceed, Object[] args)
                throws Throwable {

                return handler.intercept((T) self, thisMethod, args, new SuperMethod() {
                    public Object invoke(Object proxy, Object[] args) {
                        try {
                            return proceed.invoke(proxy, args);
                        } catch (Throwable throwable) {
                            throw new ProxyInvocationException(throwable);
                        }
                    }
                });
            }
        });
    }
}
