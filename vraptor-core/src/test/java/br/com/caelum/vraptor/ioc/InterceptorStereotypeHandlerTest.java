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
package br.com.caelum.vraptor.ioc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorStereotypeHandlerTest {

	private @Mock InterceptorRegistry interceptorRegistry;
	private @Mock ComponentRegistry componentRegistry;
	private InterceptorStereotypeHandler handler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		handler = new InterceptorStereotypeHandler(interceptorRegistry, componentRegistry);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldRegisterInterceptorsOnRegistry() throws Exception {
		handler.handle(InterceptorA.class);
		verify(interceptorRegistry, times(1)).register(InterceptorA.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldRegisterInterceptorsFromInterceptorSequenceOnBothInterceptorAndComponentRegistrys() throws Exception {
		handler.handle(MySequence.class);
		
		verify(interceptorRegistry).register(InterceptorA.class, InterceptorB.class);
		verify(componentRegistry).deepRegister(InterceptorA.class);
		verify(componentRegistry).deepRegister(InterceptorB.class);
	}

	public static class MySequence implements InterceptorSequence {
		@SuppressWarnings("unchecked")
		public Class<? extends Interceptor>[] getSequence() {
			return new Class[] { InterceptorA.class, InterceptorB.class };
		}
	}

	static class InterceptorA implements Interceptor {

		public boolean accepts(ResourceMethod method) {
			return false;
		}

		public void intercept(InterceptorStack stack, ResourceMethod method,
				Object resourceInstance) throws InterceptionException {
		}
	}
	static class InterceptorB implements Interceptor {

		public boolean accepts(ResourceMethod method) {
			return false;
		}

		public void intercept(InterceptorStack stack, ResourceMethod method,
				Object resourceInstance) throws InterceptionException {
		}
	}
}
