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

import static br.com.caelum.vraptor.view.Results.nothing;

import java.io.IOException;
import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class ExecuteMethodInterceptorTest {

	private VRaptorMockery mockery;
	private MethodInfo info;
	private InterceptorStack stack;
	private Validator validator;
	private ExecuteMethodInterceptor interceptor;

	@Before
	public void setup() throws NoSuchMethodException {
		this.mockery = new VRaptorMockery();
		this.info = mockery.mock(MethodInfo.class);
		this.stack = mockery.mock(InterceptorStack.class);
		this.validator = mockery.mock(Validator.class);
		this.interceptor = new ExecuteMethodInterceptor(info, validator);
	}

	@Test
	public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
			NoSuchMethodException, IOException, InterceptionException {
		final ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
		final DogAlike auau = mockery.mock(DogAlike.class);
		mockery.checking(new Expectations() {
			{
				one(auau).bark();
				one(info).getParameters();
				will(returnValue(new Object[] {}));
				one(stack).next(method, auau);
				one(info).setResult("ok");
				allowing(validator);
			}
		});
		interceptor.intercept(stack, method, auau);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException,
			NoSuchMethodException {
		ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
		final DogAlike auau = mockery.mock(DogAlike.class);
		final RuntimeException exception = new RuntimeException();
		mockery.checking(new Expectations() {
			{
				one(auau).bark();
				will(throwException(exception));
				one(info).getParameters();
				will(returnValue(new Object[] {}));
			}
		});
		try {
			interceptor.intercept(stack, method, auau);
			Assert.fail();
		} catch (InterceptionException e) {
			MatcherAssert.assertThat((RuntimeException) e.getCause(), Matchers.is(Matchers.equalTo(exception)));
			mockery.assertIsSatisfied();
		}
	}

	@Test
	public void shouldUseTheProvidedArguments() throws SecurityException, NoSuchMethodException, InterceptionException,
			IOException {
		final ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark", int.class));
		final DogAlike auau = mockery.mock(DogAlike.class);
		mockery.checking(new Expectations() {
			{
				one(auau).bark(3);
				one(info).getParameters();
				will(returnValue(new Object[] { 3 }));
				one(stack).next(method, auau);
				one(info).setResult("ok");
				allowing(validator);
			}
		});
		interceptor.intercept(stack, method, auau);
		mockery.assertIsSatisfied();
	}

	public static class XController {
		public Object method(Object o) {
			return o;
		}

		public void method() {

		}
	}

	@Test
	public void shouldSetResultReturnedValueFromInvokedMethod() throws SecurityException, NoSuchMethodException,
			InterceptionException, IOException {
		final ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method",
				Object.class));
		final XController x = new XController();
		mockery.checking(new Expectations() {
			{
				one(info).getParameters();
				will(returnValue(new Object[] { "string" }));
				one(stack).next(method, x);
				one(info).setResult("string");
				allowing(validator);
			}
		});
		interceptor.intercept(stack, method, x);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldSetNullWhenNullReturnedFromInvokedMethod() throws SecurityException, NoSuchMethodException,
			InterceptionException, IOException {
		final ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method",
				Object.class));
		final XController x = new XController();
		mockery.checking(new Expectations() {
			{
				one(info).getParameters();
				will(returnValue(new Object[] { null }));
				one(stack).next(method, x);
				one(info).setResult(null);
				allowing(validator);
			}
		});
		interceptor.intercept(stack, method, x);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldSetOkWhenVoidReturnedFromInvokedMethod() throws SecurityException, NoSuchMethodException,
			InterceptionException, IOException {
		final ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method"));
		final XController x = new XController();
		mockery.checking(new Expectations() {
			{
				one(info).getParameters();
				will(returnValue(new Object[] {}));
				one(stack).next(method, x);
				one(info).setResult("ok");
				allowing(validator);
			}
		});
		interceptor.intercept(stack, method, x);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldBeOkIfThereIsValidationErrorsAndYouSpecifiedWhereToGo() throws SecurityException,
			NoSuchMethodException, InterceptionException, IOException {
		final ResourceMethod method = mockery.methodFor(AnyController.class, "specifiedWhereToGo");
		final AnyController controller = new AnyController(validator);
		mockery.checking(new Expectations() {
			{
				one(info).getParameters();
				will(returnValue(new Object[0]));
				one(validator).onErrorUse(nothing());
				will(throwException(new ValidationException(Collections.<Message> emptyList())));
				allowing(validator).hasErrors();
				will(returnValue(true));
			}
		});
		interceptor.intercept(stack, method, controller);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldThrowExceptionIfYouHaventSpecifiedWhereToGoOnValidationError() throws SecurityException,
			NoSuchMethodException, InterceptionException, IOException {
		final ResourceMethod method = mockery.methodFor(AnyController.class, "didntSpecifyWhereToGo");
		final AnyController controller = new AnyController(validator);
		mockery.checking(new Expectations() {
			{
				one(info).getParameters();
				will(returnValue(new Object[0]));
				one(validator).hasErrors();
				will(returnValue(true));
			}
		});
		try {
			interceptor.intercept(stack, method, controller);
			Assert.fail();
		} catch (InterceptionException e) {
			mockery.assertIsSatisfied();
		}
	}

	public static class AnyController {
		private final Validator validator;

		public AnyController(Validator validator) {
			this.validator = validator;
		}

		public void didntSpecifyWhereToGo() {

		}

		public void specifiedWhereToGo() {
			this.validator.onErrorUse(nothing());
		}
	}

}
