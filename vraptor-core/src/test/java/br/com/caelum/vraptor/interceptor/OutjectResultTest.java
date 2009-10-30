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

import java.lang.reflect.Method;
import java.util.List;

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
	private TypeNameExtractor extractor;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.result = mockery.mock(Result.class);
		this.info = mockery.mock(MethodInfo.class);
		this.instance = null;
		this.method = mockery.mock(ResourceMethod.class);
		this.stack = mockery.mock(InterceptorStack.class);
		this.extractor = mockery.mock(TypeNameExtractor.class);

		this.interceptor = new OutjectResult(result, info, extractor);
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

				one(extractor).nameFor(String.class); will(returnValue("string"));

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
				Method myComponentMethod = MyComponent.class.getMethod("returnsStrings");
				one(method).getMethod(); will(returnValue(myComponentMethod));
				one(info).getResult(); will(returnValue("myString"));
				one(extractor).nameFor(myComponentMethod.getGenericReturnType()); will(returnValue("stringList"));
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


}
