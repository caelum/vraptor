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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Used to create proxy instances for Classes. It will decide whether to use CGLib or DynamicProxies only based on the
 * class to be proxified.
 *
 * @author Fabio Kung
 */
@ApplicationScoped
public class DefaultProxifier implements Proxifier {
    private final Logger logger = LoggerFactory.getLogger(DefaultProxifier.class);

    /**
     * Create a proxy for the given type using Java Dynamic Proxies if the type is an interface. Otherwise, proxies for
     * concrete classes are created with CGLib.
     *
     * @param type    to be proxified
     * @param handler that receives method calls
     * @return the proxy
     */
    public Object proxify(Class type, final MethodInvocation handler) {
        if (type.isInterface()) {
            return useDynamicProxy(type, handler);
        } else {
            return useCGLib(type, handler);
        }
    }

    private Object useDynamicProxy(Class type, MethodInvocation handler) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, new DynamicProxyInvocation(handler));
    }

    private Object useCGLib(Class type, MethodInvocation handler) {
        Enhancer enhancer = enhanceTypeWithCGLib(type, handler);
        Constructor[] constructors = type.getDeclaredConstructors();
        Constructor defaultConstructor = findDefaultConstructor(constructors);

        if (defaultConstructor != null) {
            logger.trace("Default constructor found in: " + type);
            return useDefaultConstructor(enhancer);
        } else {
            logger.info(String.format("No default constructor found for %s. Trying to create the proxy with other " +
                    "constructors (there are %d).", type, constructors.length));
            return tryAllConstructors(type, enhancer, constructors);
        }
    }

    private Enhancer enhanceTypeWithCGLib(Class type, final MethodInvocation handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new MethodInterceptor() {
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
        });
        return enhancer;
    }

    private Object useDefaultConstructor(Enhancer enhancer) {
        return enhancer.create();
    }

    private Object tryAllConstructors(Class type, Enhancer enhancer, Constructor[] constructors) {
        List<Throwable> problems = new ArrayList<Throwable>();
        for (Constructor constructor : constructors) {
            Class[] parameterTypes = constructor.getParameterTypes();
            Object[] parameterValues = proxyParameters(parameterTypes);

            if (logger.isTraceEnabled()) {
                logger.trace("trying constructor with following parameters types: " +
                        Arrays.toString(parameterTypes) + "values are going to be: " + Arrays.toString(parameterValues));
            }

            try {
                return enhancer.create(parameterTypes, parameterValues);
            } catch (Throwable e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Problem while calling constructor with parameters" +
                            Arrays.toString(constructor.getParameterTypes()) + ". Trying next.", e);
                }
                problems.add(e);
                continue; // try next constructor
            }
        }

        String message = String.format("Tried to instantiate type: %s %d times, but none of the attempts worked. " +
                "The exceptions are: %s", type, constructors.length, problems);
        throw new ProxyCreationException(message);
    }

    /**
     * By now, we are always passing null as constructor parameters. If needed, we can create proxies for each parameter,
     * changing this method.
     *
     * @param parameterTypes of the constructor to be called, in order.
     * @return parameter instances for the given types.
     */
    private Object[] proxyParameters(Class[] parameterTypes) {
        return new Object[parameterTypes.length];
    }

    /**
     * @param constructors from the type to be proxified
     * @return null when there isn't a default (null) constructor
     */
    private Constructor findDefaultConstructor(Constructor[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }
        return null;
    }

}
