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
