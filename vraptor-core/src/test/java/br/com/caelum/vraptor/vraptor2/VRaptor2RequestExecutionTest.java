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
package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

public class VRaptor2RequestExecutionTest {

    private Mockery mockery;
    private InterceptorStack stack;
    private VRaptor2RequestExecution execution;
    private InstantiateInterceptor instantiator;
    private Config config;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.stack = mockery.mock(InterceptorStack.class);
        this.instantiator = new InstantiateInterceptor(null);
        this.config = mockery.mock(Config.class);
        mockery.checking(new Expectations() {
            {
                one(config).hasPlugin(org.vraptor.plugin.hibernate.HibernateValidatorPlugin.class.getName());
                will(returnValue(true));
            }
        });
        this.execution = new VRaptor2RequestExecution(stack, instantiator, config);
    }

    @Test
    public void shouldAddInterceptorsInOrder() throws InterceptionException, IOException {
        final Sequence sequence = mockery.sequence("executionSequence");
        mockery.checking(new Expectations() {
            {
                one(stack).add(ResourceLookupInterceptor.class);
                inSequence(sequence);
                one(stack).add(URLParameterExtractorInterceptor.class);
                inSequence(sequence);
                one(stack).add(InterceptorListPriorToExecutionExtractor.class);
                inSequence(sequence);
                one(stack).add(DownloadInterceptor.class);
                inSequence(sequence);
                one(stack).add(MultipartInterceptor.class);
                inSequence(sequence);
                one(stack).add(instantiator);
                inSequence(sequence);
                one(stack).add(ParametersInstantiatorInterceptor.class);
                inSequence(sequence);
                one(stack).add(HibernateValidatorPluginInterceptor.class);
                inSequence(sequence);
                one(stack).add(ValidatorInterceptor.class);
                inSequence(sequence);
                one(stack).add(ExecuteMethodInterceptor.class);
                inSequence(sequence);
                one(stack).add(OutjectResult.class);
                inSequence(sequence);
                one(stack).add(OutjectionInterceptor.class);
                inSequence(sequence);
                one(stack).add(AjaxInterceptor.class);
                inSequence(sequence);
                one(stack).add(ViewInterceptor.class);
                inSequence(sequence);
                one(stack).next(null, null);
                inSequence(sequence);
            }
        });
        execution.execute();
        mockery.assertIsSatisfied();
    }

}
