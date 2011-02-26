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

import static org.mockito.Mockito.inOrder;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.DeserializingInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;

public class DefaultRequestExecutionTest {

    @Mock private InterceptorStack stack;
    private DefaultRequestExecution execution;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        this.execution = new DefaultRequestExecution(stack);
    }

    @Test
    public void shouldAddInterceptorsInOrder() throws InterceptionException, IOException {

        execution.execute();

        InOrder order = inOrder(stack);

        order.verify(stack).add(MultipartInterceptor.class);
        order.verify(stack).add(ResourceLookupInterceptor.class);
        order.verify(stack).add(FlashInterceptor.class);
        order.verify(stack).add(InterceptorListPriorToExecutionExtractor.class);
        order.verify(stack).add(InstantiateInterceptor.class);
        order.verify(stack).add(ParametersInstantiatorInterceptor.class);
        order.verify(stack).add(DeserializingInterceptor.class);
        order.verify(stack).add(ExecuteMethodInterceptor.class);
        order.verify(stack).add(OutjectResult.class);
        order.verify(stack).add(DownloadInterceptor.class);
        order.verify(stack).add(ForwardToDefaultViewInterceptor.class);
        order.verify(stack).next(null, null);
    }

}
