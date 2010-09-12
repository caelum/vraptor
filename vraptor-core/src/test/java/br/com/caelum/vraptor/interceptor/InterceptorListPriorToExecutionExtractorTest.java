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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorListPriorToExecutionExtractorTest {

    private @Mock InterceptorRegistry registry;
    private @Mock ResourceMethod method;
    private @Mock InterceptorStack stack;

    private InterceptorListPriorToExecutionExtractor extractor;

    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
    	MockitoAnnotations.initMocks(this);

        this.extractor = new InterceptorListPriorToExecutionExtractor(registry);
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
        when(registry.all()).thenReturn(Arrays.asList(FirstInterceptor.class, SecondInterceptor.class));

        extractor.intercept(this.stack, method, null);

        InOrder order = inOrder(stack);

        order.verify(stack).addAsNext(SecondInterceptor.class);
        order.verify(stack).addAsNext(FirstInterceptor.class);

        verify(stack).next(method, null);
    }

}
