package br.com.caelum.vraptor.flex;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;

@Component
@ApplicationScoped
public class VRaptorLookup {

	private static Container container;

	@Inject
	@Autowired
	public VRaptorLookup(Container container) {
		VRaptorLookup.container = container;
	}

	public VRaptorLookup() {
	}

	public Object lookup(String className) {
		Class<?> type;
		try {
			type = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Source destination does not match a class name", e);
		}

		return lookup(type);
	}

	private Object lookup(Class<?> type) {
		Proxifier proxifier = container.instanceFor(Proxifier.class);
		return proxifier.proxify(type, new InterceptorInvocation(type, container.instanceFor(InterceptorsStack.class)));
	}

	private final class InterceptorInvocation implements MethodInvocation<Object> {
		private final Class<?> type;
		private final InterceptorsStack stack;

		private InterceptorInvocation(Class<?> type, InterceptorsStack stack) {
			this.type = type;
			this.stack = stack;
		}

		public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
			try {
				MethodInfo info = container.instanceFor(MethodInfo.class);
				info.setParameters(args);

				InterceptorStack vrStack = stack.createStack();
				vrStack.add(ExecuteMethodInterceptor.class);
				vrStack.next(DefaultResourceMethod.instanceFor(type, method), container.instanceFor(type));

				return info.getResult();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Could not invoke method on " + type.getCanonicalName(), e);
			}
		}
	}

}
