package br.com.caelum.vraptor.flex;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;

public class VRaptorFactoryInstance extends FactoryInstance {

	private final Container container;

	public VRaptorFactoryInstance(FlexFactory factory, String id, ConfigMap properties, Container container) {
		super(factory, id, properties);
		this.container = container;
	}

	@Override
	public Object lookup() {

		final Class<?> type;
		try {
			type = Class.forName(getSource());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Source destination does not match a class name", e);
		}

		Proxifier proxifier = container.instanceFor(Proxifier.class);
		Object proxy = proxifier.proxify(type,
				new InterceptorInvocation(type, container.instanceFor(InterceptorsStack.class)));

		return proxy;
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
