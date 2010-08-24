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
package br.com.caelum.vraptor.core;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorStackTest {

	private static final ResourceMethod A_METHOD = null;
	private static final Object AN_INSTANCE = null;

	private @Mock InterceptorHandlerFactory handlerFactory;
	private @Mock(name = "first") InterceptorHandler firstHandler;
	private @Mock(name = "second") InterceptorHandler secondHandler;

	private DefaultInterceptorStack stack;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		stack = new DefaultInterceptorStack(handlerFactory);
		when(handlerFactory.handlerFor(FirstInterceptor.class)).thenReturn(firstHandler);
		when(handlerFactory.handlerFor(SecondInterceptor.class)).thenReturn(secondHandler);
	}

	static interface FirstInterceptor extends Interceptor {}
	static interface SecondInterceptor extends Interceptor {}

    @Test
    public void testInvokesAllInterceptorsInItsCorrectOrder() throws IOException, InterceptionException {
        stack.add(FirstInterceptor.class);
        stack.add(SecondInterceptor.class);

        stack.next(A_METHOD, AN_INSTANCE);
        verify(firstHandler).execute(stack, A_METHOD, AN_INSTANCE);

        stack.next(A_METHOD, AN_INSTANCE);
        verify(secondHandler).execute(stack, A_METHOD, AN_INSTANCE);
    }

    @Test
    public void shouldAddNextInterceptorAsNext() throws InterceptionException, IOException {
        stack.add(FirstInterceptor.class);
        stack.addAsNext(SecondInterceptor.class);

        stack.next(A_METHOD, AN_INSTANCE);
        verify(secondHandler).execute(stack, A_METHOD, AN_INSTANCE);

        stack.next(A_METHOD, AN_INSTANCE);
        verify(firstHandler).execute(stack, A_METHOD, AN_INSTANCE);
    }

}
