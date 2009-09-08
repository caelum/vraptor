package br.com.caelum.vraptor.util.test;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.view.PageResult;

public class MockedPage implements PageResult {

	private final Proxifier proxifier;

	public MockedPage() {
		proxifier = new ObjenesisProxifier();
	}

	public void forward() {

	}

	public void forward(String url) {

	}

	public void include() {
	}

	public void redirect(String url) {

	}

	public <T> T of(Class<T> controllerType) {
		return proxifier.proxify(controllerType, new MethodInvocation<T>() {

			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				return null;
			}

		});
	}

}
