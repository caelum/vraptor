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

package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class OutjectResultTest {

	private VRaptorMockery mockery;
	private Result result;
	private MethodInfo info;
	private OutjectResult interceptor;
	private ResourceMethod method;
	private Object instance;
	private InterceptorStack stack;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.result = mockery.mock(Result.class);
		this.info = mockery.mock(MethodInfo.class);
		this.instance = null;
		this.method = mockery.mock(ResourceMethod.class);
		this.stack = mockery.mock(InterceptorStack.class);
		this.interceptor = new OutjectResult(result, info);
		mockery.checking(new Expectations() {
			{
				one(stack).next(method, instance);
			}
		});
	}

	interface MyComponent {
		String returnsAString();
		List<String> returnsStrings();
		void noReturn();
	}

	@Test
	public void shouldOutjectWithASimpleTypeName() throws NoSuchMethodException {
		mockery.checking(new Expectations() {
			{
				one(method).getMethod(); will(returnValue(MyComponent.class.getMethod("returnsAString")));
				one(info).getResult(); will(returnValue("myString"));
				one(result).include("string", "myString");
			}
		});
		interceptor.intercept(stack, method, instance);
		mockery.assertIsSatisfied();
	}


	@Test
	public void shouldOutjectACollectionAsAList() throws NoSuchMethodException {
		mockery.checking(new Expectations() {
			{
				one(method).getMethod(); will(returnValue(MyComponent.class.getMethod("returnsStrings")));
				one(info).getResult(); will(returnValue("myString"));
				one(result).include("stringList", "myString");
			}
		});
		interceptor.intercept(stack, method, instance);
		mockery.assertIsSatisfied();
	}


	@Test
	public void shouldNotOutjectIfThereIsNoReturnType() throws NoSuchMethodException {
		mockery.checking(new Expectations() {
			{
				one(method).getMethod(); will(returnValue(MyComponent.class.getMethod("noReturn")));
			}
		});
		interceptor.intercept(stack, method, instance);
		mockery.assertIsSatisfied();
	}



	static class AClass {}

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercased() throws NoSuchMethodException {
		Assert.assertEquals("urlClassLoader",interceptor.nameFor(URLClassLoader.class));
		Assert.assertEquals("bigDecimal",interceptor.nameFor(BigDecimal.class));
		Assert.assertEquals("string",interceptor.nameFor(String.class));
		Assert.assertEquals("aClass",interceptor.nameFor(AClass.class));
		Assert.assertEquals("url",interceptor.nameFor(URL.class));
	}


	ArrayList<URLClassLoader> urls;
	HashSet<BigDecimal> bigs;
	HashSet<? extends BigDecimal> bigsLimited;
	HashSet<? super BigDecimal> bigsLimited2;
	HashSet<?> objects;
	HashSet bigsOld;
	Vector<String> strings;
	Class<String> clazz;

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercasedForListsAndArrays() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		Assert.assertEquals("stringList",interceptor.nameFor(getField("strings")));
		Assert.assertEquals("bigDecimalList",interceptor.nameFor(getField("bigs")));
		Assert.assertEquals("hashSet",interceptor.nameFor(getField("bigsOld")));
		Assert.assertEquals("class",interceptor.nameFor(getField("clazz")));
		Assert.assertEquals("aClassList",interceptor.nameFor(AClass[].class));
		Assert.assertEquals("urlClassLoaderList",interceptor.nameFor(getField("urls")));
	}

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercasedForListsAndArraysForBoundedGenericElements() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		Assert.assertEquals("bigDecimalList",interceptor.nameFor(getField("bigsLimited")));
		Assert.assertEquals("bigDecimalList",interceptor.nameFor(getField("bigsLimited2")));
		Assert.assertEquals("objectList",interceptor.nameFor(getField("objects")));
	}


	private Type getField(String string) throws SecurityException, NoSuchFieldException {
		return this.getClass().getDeclaredField(string).getGenericType();
	}

}
