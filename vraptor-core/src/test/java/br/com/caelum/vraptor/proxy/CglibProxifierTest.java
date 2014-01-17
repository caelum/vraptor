/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class CglibProxifierTest {

	private Proxifier proxifier;
	
	@Before
	public void setUp() throws Exception {
	proxifier= new JavassistProxifier(new ObjenesisInstanceCreator());
	}
	
	@Test
	public void shouldProxifyInterfaces() {
	TheInterface proxy = proxifier.proxify(TheInterface.class, new MethodInvocation<TheInterface>() {
		public Object intercept(TheInterface proxy, Method method, Object[] args, SuperMethod superMethod) {
		return true;
		}
	});
	assertThat(proxy.wasCalled(), is(true));
	}

	@Test
	public void shouldProxifyConcreteClassesWithDefaultConstructors() {
	TheClass proxy = proxifier.proxify(TheClass.class, new MethodInvocation<TheClass>() {
		public Object intercept(TheClass proxy, Method method, Object[] args, SuperMethod superMethod) {
		return true;
		}
	});
	assertThat(proxy.wasCalled(), is(true));
	}

	@Test
	public void shouldProxifyConcreteClassesWithComplexConstructorsAndPassNullForAllParameters() {
	TheClassWithComplexConstructor proxy = proxifier.proxify(TheClassWithComplexConstructor.class, new MethodInvocation<TheClassWithComplexConstructor>() {
		public Object intercept(TheClassWithComplexConstructor proxy, Method method, Object[] args, SuperMethod superMethod) {
		return superMethod.invoke(proxy, args);
		}
	});
	assertThat(proxy.getFirstDependency(), is(nullValue()));
	assertThat(proxy.getSecondDependency(), is(nullValue()));
	}

	@Test
	public void shouldNeverCallSuperclassConstructors() {
	TheClassWithManyConstructors proxy = proxifier.proxify(TheClassWithManyConstructors.class, new MethodInvocation<TheClassWithManyConstructors>() {
		public Object intercept(TheClassWithManyConstructors proxy, Method method, Object[] args, SuperMethod superMethod) {
		return superMethod.invoke(proxy, args);
		}
	});
	assertThat(proxy.wasNumberConstructorCalled(), is(false));
	assertThat(proxy.getNumber(), is(nullValue()));
	}

	@Test
	public void shouldNotProxifyJavaLangObjectMethods() throws Exception {
	Object proxy = proxifier.proxify(CglibProxifierTest.class, new MethodInvocation<Object>() {
		public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
		fail("should not call this Method interceptor");
		return null;
		}
	});
	new Mirror().on(proxy).invoke().method("finalize").withoutArgs();
	}

	@Test
	public void testIfObjectIsProxy() {
	Object realObject = new Object();
	Object objectAsProxy = proxifier.proxify(Object.class, new MethodInvocation<Object>() {
		public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
		return null;
		}
	});
	
	assertThat(proxifier.isProxy(null), is(false));
	assertThat(proxifier.isProxy(realObject), is(false));
	assertThat(proxifier.isProxy(objectAsProxy), is(true));
	}
	
	@Test
	public void shouldThrowProxyInvocationExceptionIfAnErrorOccurs() {
		C proxy = proxifier.proxify(C.class, new MethodInvocation<C>() {
			public Object intercept(C proxy, Method method, Object[] args, SuperMethod superMethod) {
				return superMethod.invoke(proxy, args);
			}
		});
		
		try {
			proxy.doThrow();
			fail("Should throw exception");
		} catch (ProxyInvocationException e) {
			
		}
	}

	@Test
	public void shouldNotProxifyBridges() throws Exception {
		B proxy = proxifier.proxify(B.class, new MethodInvocation<B>() {
			public Object intercept(B proxy, Method method, Object[] args, SuperMethod superMethod) {
				if (method.isBridge()) {
					fail("Method " + method + " is a bridge");
				}
				return null;
			}
		});
		
		Method[] methods = proxy.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals("getT")) {
				m.invoke(proxy, "");
			}
		}
	}

	public static class A<T> {
		public T getT(T t) { return t; }
	}

	static class B extends A<String> {
		public String getT(String s) { return s; }
	}
	
	static class C {
		public String doThrow() { throw new IllegalStateException(); }
	}
}
