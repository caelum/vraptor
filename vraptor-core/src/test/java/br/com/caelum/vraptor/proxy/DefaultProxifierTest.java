/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.proxy;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class DefaultProxifierTest {

    @Test
    public void shouldProxifyInterfaces() {
        Proxifier proxifier = new DefaultProxifier();
        TheInterface proxy = (TheInterface) proxifier.proxify(TheInterface.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithDefaultConstructors() {
        Proxifier proxifier = new DefaultProxifier();
        TheClass proxy = (TheClass) proxifier.proxify(TheClass.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithComplexConstructorsAndPassNullForAllParameters() {
        Proxifier proxifier = new DefaultProxifier();
        TheClassWithComplexConstructor proxy = (TheClassWithComplexConstructor) proxifier.proxify(TheClassWithComplexConstructor.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertThat(proxy.getFirstDependency(), is(nullValue()));
        assertThat(proxy.getSecondDependency(), is(nullValue()));
    }

    @Test
    public void shouldTryAllConstructorsInDeclarationOrder() {
        Proxifier proxifier = new DefaultProxifier();
        TheClassWithManyConstructors proxy = (TheClassWithManyConstructors) proxifier.proxify(TheClassWithManyConstructors.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertTrue(proxy.wasNumberConstructorCalled());
        assertThat(proxy.getNumber(), is(nullValue()));
    }

}
