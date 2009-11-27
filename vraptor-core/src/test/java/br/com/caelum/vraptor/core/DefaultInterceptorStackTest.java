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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorStackTest {

    private int count;

    @Before
    public void setup() {
        count = 0;
    }

    @Test
    public void testInvokesAllInterceptorsInItsCorrectOrder() throws IOException, InterceptionException {
        DefaultInterceptorStack stack = new DefaultInterceptorStack(null);
        CountInterceptor first = new CountInterceptor();
        CountInterceptor second = new CountInterceptor();
        stack.add(first);
        stack.add(second);
        stack.next(null, null);
        assertThat(first.run, is(equalTo(0)));
        assertThat(second.run, is(equalTo(1)));
    }

    class CountInterceptor implements Interceptor {
        int run;

        public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
            run = count++;
            invocation.next(method, resourceInstance);
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    @Test
    public void shouldAddNextInterceptorAsNext() throws InterceptionException, IOException {
        Interceptor firstMocked = mock(Interceptor.class, "firstMocked");
        final Interceptor secondMocked = mock(Interceptor.class, "secondMocked");
        final DefaultInterceptorStack stack = new DefaultInterceptorStack(null);
        stack.add(firstMocked);
        stack.addAsNext(secondMocked);

        when(secondMocked.accepts(null)).thenReturn(true);

        stack.next(null, null);

        verify(secondMocked).intercept(stack, null, null);
    }

    @Test
    public void shouldAddInterceptorAsLast() throws InterceptionException, IOException {
        final Interceptor firstMocked = mock(Interceptor.class, "firstMocked");
        final Interceptor secondMocked = mock(Interceptor.class, "secondMocked");
        final DefaultInterceptorStack stack = new DefaultInterceptorStack(null);
        stack.add(firstMocked);
        stack.add(secondMocked);

        when(firstMocked.accepts(null)).thenReturn(true);

        stack.next(null, null);

        verify(firstMocked).intercept(stack, null, null);
    }

}
