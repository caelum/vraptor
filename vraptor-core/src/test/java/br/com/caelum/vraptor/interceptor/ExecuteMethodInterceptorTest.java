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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class ExecuteMethodInterceptorTest {

	private @Mock MethodInfo info;
	private @Mock InterceptorStack stack;
	private @Mock Validator validator;
	private ExecuteMethodInterceptor interceptor;

	@Before
	public void setup() throws NoSuchMethodException {
		MockitoAnnotations.initMocks(this);
		interceptor = new ExecuteMethodInterceptor(info, validator);
	}

	@Test
	public void shouldAcceptAlways() {
		assertTrue(interceptor.accepts(null));
	}
	
	@Test
	public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
			NoSuchMethodException, IOException, InterceptionException {
		ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
		DogAlike auau = mock(DogAlike.class);
		when(info.getParameters()).thenReturn(new Object[0]);
		
		interceptor.intercept(stack, method, auau);
		
		verify(auau).bark();
		verify(stack).next(method, auau);
		verify(info).setResult("ok");
	}

	@Test
	public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException,
			NoSuchMethodException {
		ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
		final DogAlike auau = mock(DogAlike.class);
		final RuntimeException exception = new RuntimeException();
		
		doThrow(exception).when(auau).bark();
		when(info.getParameters()).thenReturn(new Object[0]);
		
		try {
			interceptor.intercept(stack, method, auau);
			Assert.fail();
		} catch (InterceptionException e) {
			assertThat((RuntimeException) e.getCause(), is(equalTo(exception)));
		}
	}

	@Test
	public void shouldUseTheProvidedArguments() throws SecurityException, NoSuchMethodException, InterceptionException,
			IOException {
		ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark", int.class));
		DogAlike auau = mock(DogAlike.class);

		when(info.getParameters()).thenReturn(new Object[] { 3 });

		interceptor.intercept(stack, method, auau);

		verify(auau).bark(3);
		verify(stack).next(method, auau);
		verify(info).setResult("ok");
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
		ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method", Object.class));
		final XController x = new XController();
		
		when(info.getParameters()).thenReturn(new Object[] { "string" });

		interceptor.intercept(stack, method, x);

		verify(stack).next(method, x);
		verify(info).setResult("string");
	}

	@Test
	public void shouldSetNullWhenNullReturnedFromInvokedMethod() throws SecurityException, NoSuchMethodException,
			InterceptionException, IOException {
		ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method", Object.class));
		final XController x = new XController();
		
		when(info.getParameters()).thenReturn(new Object[] { null });

		interceptor.intercept(stack, method, x);

		verify(stack).next(method, x);
		verify(info).setResult(null);
	}

	@Test
	public void shouldSetOkWhenVoidReturnedFromInvokedMethod() throws SecurityException, NoSuchMethodException,
			InterceptionException, IOException {
		ResourceMethod method = new DefaultResourceMethod(null, XController.class.getMethod("method"));
		XController x = new XController();
		
		when(info.getParameters()).thenReturn(new Object[] {});
		
		interceptor.intercept(stack, method, x);
		
		verify(stack).next(method, x);
		verify(info).setResult("ok");
	}

	@Test
	public void shouldBeOkIfThereIsValidationErrorsAndYouSpecifiedWhereToGo() throws SecurityException,
			NoSuchMethodException, InterceptionException, IOException {
		Method specifiedWhereToGo = AnyController.class.getMethod("specifiedWhereToGo");
		ResourceMethod method = DefaultResourceMethod.instanceFor(AnyController.class, specifiedWhereToGo);
		AnyController controller = new AnyController(validator);
		
		when(info.getParameters()).thenReturn(new Object[0]);
		doThrow(new ValidationException(Collections.<Message> emptyList())).when(validator).onErrorUse(nothing());
		when(validator.hasErrors()).thenReturn(true);
				
		interceptor.intercept(stack, method, controller);
	}

	@Test
	public void shouldThrowExceptionIfYouHaventSpecifiedWhereToGoOnValidationError() throws SecurityException,
			NoSuchMethodException, InterceptionException, IOException {
		Method didntSpecifyWhereToGo = AnyController.class.getMethod("didntSpecifyWhereToGo");
		final ResourceMethod method = DefaultResourceMethod.instanceFor(AnyController.class, didntSpecifyWhereToGo);
		final AnyController controller = new AnyController(validator);
		
		when(info.getParameters()).thenReturn(new Object[0]);
		when(validator.hasErrors()).thenReturn(true);
				
		try {
			interceptor.intercept(stack, method, controller);
			Assert.fail();
		} catch (InterceptionException e) {
			
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
