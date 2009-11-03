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

import java.io.IOException;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class InterceptorListPriorToExecutionExtractorTest {

    private VRaptorMockery mockery;
    private InterceptorRegistry registry;
    private InterceptorListPriorToExecutionExtractor extractor;
    private ResourceMethod method;
    private InterceptorStack stack;
    private Container container;

    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        this.mockery = new VRaptorMockery();
        this.container = mockery.mock(Container.class);
        this.registry = mockery.mock(InterceptorRegistry.class);
        this.extractor = new InterceptorListPriorToExecutionExtractor(registry, container);
        this.method = mockery.mock(ResourceMethod.class);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    public static class FirstInterceptor implements Interceptor {
		public boolean accepts(ResourceMethod method) {
			return false;
		}
		public void intercept(InterceptorStack stack, ResourceMethod method,
				Object resourceInstance) throws InterceptionException {
		}
    }
    public static class SecondInterceptor implements Interceptor {
    	public boolean accepts(ResourceMethod method) {
    		return false;
    	}
    	public void intercept(InterceptorStack stack, ResourceMethod method,
    			Object resourceInstance) throws InterceptionException {
    	}
    }
    @Test
    public void shouldAddTheListOfInterceptorsAsFollowingInterceptors() throws InterceptionException, IOException {
        final Interceptor[] array = {new FirstInterceptor(), new SecondInterceptor()};
        mockery.checking(new Expectations() {
            {
                one(registry).interceptorsFor(method, container);
                will(returnValue(array));
                for (Interceptor i : array) {
                    one(stack).addAsNext(i);
                }
                one(stack).next(method, null);
            }
        });
        extractor.intercept(this.stack, method, null);
        mockery.assertIsSatisfied();
    }

    @Test
	public void shouldExecuteInterceptorsInOrder() throws Exception {
    	final Interceptor first = mockery.mock(Interceptor.class, "first");
		final Interceptor second = mockery.mock(Interceptor.class, "second");
		final Interceptor[] array = {first, second};
		final DefaultInterceptorStack stack = new DefaultInterceptorStack(container);
        mockery.checking(new Expectations() {
            {
                one(registry).interceptorsFor(method, container);
                will(returnValue(array));
                allowing(first).accepts(method); will(returnValue(true));
                allowing(second).accepts(method); will(returnValue(true));

                Sequence sequence = mockery.sequence("interceptors");
                one(first).intercept(stack, method, null);
                will(continueStack(stack)); inSequence(sequence);

                one(second).intercept(stack, method, null);
                will(continueStack(stack)); inSequence(sequence);

            }
        });
        extractor.intercept(stack, method, null);


        mockery.assertIsSatisfied();
	}

    private Action continueStack(final DefaultInterceptorStack stack) {
		return new Action() {
			public Object invoke(Invocation invocation) throws Throwable {
				stack.next(method, null);
				return null;
			}
			public void describeTo(Description description) {
				description.appendText("continue stack");
			}
		};
	}
}
