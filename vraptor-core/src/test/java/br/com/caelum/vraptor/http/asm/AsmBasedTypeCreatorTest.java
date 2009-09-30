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
package br.com.caelum.vraptor.http.asm;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.DefaultParameterNameProvider;
import br.com.caelum.vraptor.interceptor.DogAlike;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class AsmBasedTypeCreatorTest {

    private AsmBasedTypeCreator creator;
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.creator = new AsmBasedTypeCreator(new DefaultParameterNameProvider());
        this.mockery = new VRaptorMockery();
    }

    @Test
    public void testShouldCreateGetterAndSetterForAMethodParameter() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("bark", String.class)));

        Method getter = type.getDeclaredMethod("getString");
        Method setter = type.getDeclaredMethod("setString", String.class);

        Object instance = type.newInstance();
        setter.invoke(instance, "MESSAGE");
        MatcherAssert.assertThat((String) getter.invoke(instance), Matchers.is(Matchers.equalTo("MESSAGE")));
        mockery.assertIsSatisfied();
    }
    @Test
    public void createdTypeShouldBeSerializable() throws Exception {
    	Class<?> type = creator.typeFor(mockery.methodFor(DogAlike.class, "bark", String.class));
    	assertThat(type, typeCompatibleWith(Serializable.class));
    }

    @Test
    public void testShouldWorkFineIfMemberNameIsAReservedWordAsInt() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("bark", int.class)));

        Method getter = type.getDeclaredMethod("getInt");
        Method setter = type.getDeclaredMethod("setInt", int.class);

        Object instance = type.newInstance();
        setter.invoke(instance, 3);
        MatcherAssert.assertThat((Integer) getter.invoke(instance), Matchers.is(Matchers.equalTo(3)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToDealWithGenericCollection() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("eat", List.class)));

        Method getter = type.getDeclaredMethod("getListOfString");
        Assert.assertTrue(getter.getGenericReturnType() instanceof ParameterizedType);
        ParameterizedType returnType = (ParameterizedType) getter.getGenericReturnType();
        Assert.assertTrue(List.class.isAssignableFrom((Class<?>) returnType.getRawType()));
        Type firstType = returnType.getActualTypeArguments()[0];
        Assert.assertTrue(String.class.isAssignableFrom((Class<?>) firstType));

        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfPrimitive() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("dropDead", int[].class)));

        Method getter = type.getDeclaredMethod("getInt");
        Method setter = type.getDeclaredMethod("setInt", int[].class);

        Object instance = type.newInstance();
        int[] array = new int[]{0, 1};
        setter.invoke(instance, new Object[]{array});
        MatcherAssert.assertThat((int[]) getter.invoke(instance), Matchers.is(Matchers.equalTo(array)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfType() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("recurse", DogAlike[].class)));

        Method getter = type.getDeclaredMethod("getDogAlike");
        Method setter = type.getDeclaredMethod("setDogAlike", DogAlike[].class);

        Object instance = type.newInstance();
        DogAlike[] array = new DogAlike[]{mockery.mock(DogAlike.class)};
        setter.invoke(instance, new Object[]{array});
        MatcherAssert.assertThat((DogAlike[]) getter.invoke(instance), Matchers.is(Matchers.equalTo(array)));
        mockery.assertIsSatisfied();
    }
    @Test
    public void shouldHandlePrimitiveLong() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("jump", long.class)));

    	Method getter = type.getDeclaredMethod("getLong");
    	Method setter = type.getDeclaredMethod("setLong", long.class);

    	Object instance = type.newInstance();
    	long l = 0l;
    	setter.invoke(instance, l);
    	assertEquals(getter.invoke(instance), l);
    	mockery.assertIsSatisfied();
    }
    @Test
    public void shouldHandlePrimitiveDouble() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("dig", double.class)));

    	Method getter = type.getDeclaredMethod("getDouble");
    	Method setter = type.getDeclaredMethod("setDouble", double.class);

    	Object instance = type.newInstance();
    	double d = 1.0;
    	setter.invoke(instance, d);
    	assertEquals(getter.invoke(instance), d);
    	mockery.assertIsSatisfied();
    }
    @Test
    public void shouldHandlePrimitiveFloat() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("dig", float.class)));

    	Method getter = type.getDeclaredMethod("getFloat");
    	Method setter = type.getDeclaredMethod("setFloat", float.class);

    	Object instance = type.newInstance();
    	float f = 1.0f;
    	setter.invoke(instance, f);
    	assertEquals(getter.invoke(instance), f);
    	mockery.assertIsSatisfied();
    }

}
