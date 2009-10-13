/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class ObjenesisProxifierTest {

    @Test
    public void shouldProxifyInterfaces() {
        Proxifier proxifier = new ObjenesisProxifier();
        TheInterface proxy = (TheInterface) proxifier.proxify(TheInterface.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithDefaultConstructors() {
        Proxifier proxifier = new ObjenesisProxifier();
        TheClass proxy = (TheClass) proxifier.proxify(TheClass.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithComplexConstructorsAndPassNullForAllParameters() {
        Proxifier proxifier = new ObjenesisProxifier();
        TheClassWithComplexConstructor proxy = (TheClassWithComplexConstructor) proxifier.proxify(TheClassWithComplexConstructor.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertThat(proxy.getFirstDependency(), is(nullValue()));
        assertThat(proxy.getSecondDependency(), is(nullValue()));
    }

    @Test
    public void shouldNeverCallSuperclassConstructors() {
        Proxifier proxifier = new ObjenesisProxifier();
        TheClassWithManyConstructors proxy = (TheClassWithManyConstructors) proxifier.proxify(TheClassWithManyConstructors.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertFalse(proxy.wasNumberConstructorCalled());
        assertThat(proxy.getNumber(), is(nullValue()));
    }

    @Test
	public void shouldNotProxifyJavaLangObjectMethods() throws Exception {
    	Proxifier proxifier = new ObjenesisProxifier();
    	Object proxy = proxifier.proxify(ObjenesisProxifierTest.class, new MethodInvocation() {
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				Assert.fail("should not call this Method interceptor");
				return null;
			}
		});
    	new Mirror().on(proxy).invoke().method("finalize").withoutArgs();
	}

}
