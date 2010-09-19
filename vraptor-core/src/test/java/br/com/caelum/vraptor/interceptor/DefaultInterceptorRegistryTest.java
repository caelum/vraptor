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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorRegistryTest {

    private Mockery mockery;
    private DefaultInterceptorRegistry registry;
    private ResourceMethod method;
    private Container container;
    private Interceptor interceptor;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = mockery.mock(Container.class);
        this.method = mockery.mock(ResourceMethod.class);
        this.registry = new DefaultInterceptorRegistry();
        this.interceptor = mockery.mock(Interceptor.class);
    }

    public static class CustomInterceptor implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldReturnAnInterceptorWhichAcceptsTheGivenResource() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                allowing(container).instanceFor(CustomInterceptor.class);
                will(returnValue(interceptor));
                allowing(interceptor).accepts(method);
                will(returnValue(true));
            }
        });
        registry.register(CustomInterceptor.class);
        List<Interceptor> types = registry.interceptorsFor(method, container);
        assertThat(types, hasItems(interceptor));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldNotReturnAnInterceptorWhichDoesNotAcceptTheGivenResource() {
        mockery.checking(new Expectations() {
            {
            	allowing(container).instanceFor(CustomInterceptor.class);
                will(returnValue(interceptor));
                allowing(interceptor).accepts(method);
                will(returnValue(false));
            }
        });
        registry.register(CustomInterceptor.class);
        List<Interceptor> types = registry.interceptorsFor(method, container);
        assertThat(types, hasSize(0));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldReturnNoInterceptorIfThereIsNoneRegistered() {
        List<Interceptor> types = registry.interceptorsFor(method, container);
        assertThat(types, hasSize(0));
        mockery.assertIsSatisfied();
    }

}
