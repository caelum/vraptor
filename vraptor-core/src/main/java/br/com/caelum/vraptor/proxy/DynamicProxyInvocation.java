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
