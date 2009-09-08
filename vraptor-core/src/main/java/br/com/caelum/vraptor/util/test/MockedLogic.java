package br.com.caelum.vraptor.util.test;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.view.LogicResult;

public class MockedLogic implements LogicResult {
	private final Proxifier proxifier;

	public MockedLogic() {
		proxifier = new ObjenesisProxifier();
	}

	public <T> T forwardTo(Class<T> type) {
		return mock(type);
	}

	private <T> T mock(Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				return null;
			}
		});
	}

	public <T> T redirectTo(Class<T> type) {
		return mock(type);
	}

}
