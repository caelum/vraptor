
package br.com.caelum.vraptor.util.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.view.Results;

/**
 *
 * A mocked Result for testing your controllers.
 *
 * It will ignore redirections and accumulates included objects,
 * so you can use result.included() for inspect included objects.
 *
 * @author Lucas Cavalcanti
 * @author Suilherme Silveira
 */
public class MockResult implements Result {

	private final Map<String, Object> values = new HashMap<String, Object>();
	private Class<?> typeToUse;

	public void include(String key, Object value) {
		this.values.put(key, value);
	}

	public <T extends View> T use(final Class<T> view) {
		this.typeToUse = view;
		if (view.equals(Results.page())) {
			return view.cast(new MockedPage());
		}
		if (view.equals(Results.nothing())) {
			return null;
		}
		if (view.equals(Results.logic())) {
		    return view.cast(new MockedLogic());
		}
		return new ObjenesisProxifier().proxify(view, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                return null;
            }
        });
	}

	public boolean used() {
		return typeToUse != null;
	}

	/**
	 *
	 * @param key
	 * @return the value if it was included
	 */
	@SuppressWarnings("unchecked")
	public <T> T included(String key) {
		return (T) values.get(key);
	}
}
