package br.com.caelum.vraptor.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.ObjenesisStd;

public class ObjenesisProxifier implements Proxifier {

    private static final CallbackFilter IGNORE_BRIDGE_METHODS = new CallbackFilter() {
        public int accept(Method method) {
            return method.isBridge() ? 1 : 0;
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
        enhancer.setCallbackFilter(IGNORE_BRIDGE_METHODS);
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
