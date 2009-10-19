package br.com.caelum.vraptor.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

public abstract class AbstractCglibProxifier implements Proxifier {

	private static final List<Method> OBJECT_METHODS = Arrays.asList(Object.class.getDeclaredMethods());
	private static final CallbackFilter IGNORE_BRIDGE_AND_OBJECT_METHODS = new CallbackFilter() {
	        public int accept(Method method) {
	            return method.isBridge() || OBJECT_METHODS.contains(method) ? 1 : 0;
	        }
	    };

	protected Enhancer enhanceTypeWithCGLib(Class type, final MethodInvocation handler) {
	    Enhancer enhancer = new Enhancer();
	    enhancer.setSuperclass(type);
	    enhancer.setCallbackFilter(IGNORE_BRIDGE_AND_OBJECT_METHODS);
	    enhancer.setCallbackTypes(new Class[] {MethodInterceptor.class, NoOp.class});
	    return enhancer;
	}

	protected MethodInterceptor cglibMethodInterceptor(final MethodInvocation handler) {
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