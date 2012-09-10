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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ToInstantiateInterceptorHandlerTest {

	private @Mock Container container;
	private @Mock Interceptor interceptor;
	private @Mock InterceptorStack stack;
	private @Mock ResourceMethod method;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    public static class MyWeirdInterceptor implements Interceptor {
        public MyWeirdInterceptor(Dependency d) {
        }

        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    public static class Dependency {

    }

    @Test(expected = InterceptionException.class)
    public void shouldComplainWhenUnableToInstantiateAnInterceptor() throws InterceptionException, IOException {
        when(container.instanceFor(MyWeirdInterceptor.class)).thenReturn(null);
        
        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container,
                MyWeirdInterceptor.class);
        handler.execute(null, null, null);
    }

    @Test
    public void shouldInvokeInterceptorsMethodIfAbleToInstantiateIt() throws InterceptionException, IOException {
        final Object instance = new Object();
        
        when(container.instanceFor(Interceptor.class)).thenReturn(interceptor);
        when(interceptor.accepts(method)).thenReturn(true);

        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class);
        handler.execute(stack, method, instance);

        verify(interceptor).intercept(stack, method, instance);
    }
    @Test
    public void shouldNotInvokeInterceptorsMethodIfInterceptorDoesntAcceptsResource() throws InterceptionException, IOException {
    	final Object instance = new Object();
        when(container.instanceFor(Interceptor.class)).thenReturn(interceptor);
        when(interceptor.accepts(method)).thenReturn(false);

    	ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class);
    	handler.execute(stack, method, instance);
    	
        verify(interceptor, never()).intercept(stack, method, instance);
        verify(stack).next(method, instance);
    }
}
