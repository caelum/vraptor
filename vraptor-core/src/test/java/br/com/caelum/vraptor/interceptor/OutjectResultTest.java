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
package br.com.caelum.vraptor.interceptor;

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

}
