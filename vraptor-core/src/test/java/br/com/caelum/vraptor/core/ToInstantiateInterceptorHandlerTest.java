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

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ToInstantiateInterceptorHandlerTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
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
        Container container = mockery.container(MyWeirdInterceptor.class, null);
        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container,
                MyWeirdInterceptor.class);
        handler.execute(null, null, null);
    }

    @Test
    public void shouldInvokeInterceptorsMethodIfAbleToInstantiateIt() throws InterceptionException, IOException {
        final Interceptor interceptor = mockery.mock(Interceptor.class);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final Object instance = new Object();
        Container container = mockery.container(Interceptor.class, interceptor);
        mockery.checking(new Expectations() {
            {
            	one(interceptor).accepts(method); will(returnValue(true));
                one(interceptor).intercept(stack, method, instance);
            }
        });
        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class);
        handler.execute(stack, method, instance);
        mockery.assertIsSatisfied();
    }
    @Test
    public void shouldNotInvokeInterceptorsMethodIfInterceptorDoesntAcceptsResource() throws InterceptionException, IOException {
    	final Interceptor interceptor = mockery.mock(Interceptor.class);
    	final InterceptorStack stack = mockery.mock(InterceptorStack.class);
    	final ResourceMethod method = mockery.mock(ResourceMethod.class);
    	final Object instance = new Object();
    	Container container = mockery.container(Interceptor.class, interceptor);
    	mockery.checking(new Expectations() {
    		{
    			one(interceptor).accepts(method); will(returnValue(false));

    			never(interceptor).intercept(stack, method, instance);

    			one(stack).next(method, instance);
    		}
    	});
    	ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class);
    	handler.execute(stack, method, instance);
    	mockery.assertIsSatisfied();
    }

}
