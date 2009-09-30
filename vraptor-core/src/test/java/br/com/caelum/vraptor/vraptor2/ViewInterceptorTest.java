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

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.view.PageResult;

public class ViewInterceptorTest {

    private VRaptorMockery mockery;
    private PageResult result;
    private ViewInterceptor interceptor;
    private ComponentInfoProvider info;
    private ResourceMethod method;
	private InterceptorStack stack;

    @Component
    class VRaptor2Component {
    	public void method() {
    	}
    }
    @Resource
    class VRaptor3Resource {
    	public void method() {
    	}
    }
    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.result = mockery.mock(PageResult.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.stack = mockery.mock(InterceptorStack.class);
        this.interceptor = new ViewInterceptor(result, info);
    }

    @Test
    public void shouldForward() throws SecurityException, InterceptionException, NoSuchMethodException {
        this.method = mockery.methodFor(VRaptor2Component.class, "method");
        mockery.checking(new Expectations() {
            {
                one(info).shouldShowView(method); will(returnValue(true));
                one(result).forward();
            }
        });
        interceptor.intercept(null, this.method, null);
        mockery.assertIsSatisfied();
    }
    @Test
    public void shouldInvokeNextIfVRaptor3Resource() throws SecurityException, InterceptionException, NoSuchMethodException {
        this.method = mockery.methodFor(VRaptor3Resource.class, "method");
        final VRaptor3Resource instance = new VRaptor3Resource();
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, instance);
            }
        });
        interceptor.intercept(stack, this.method, instance);
        mockery.assertIsSatisfied();
    }
    class MyThrowable extends RuntimeException{
        private static final long serialVersionUID = 1L;
        
    }


    @Test
    public void doesNothingInAViewlessMethodResource() throws SecurityException, InterceptionException, NoSuchMethodException {
        this.method = mockery.methodFor(VRaptor2Component.class, "method");
        mockery.checking(new Expectations() {
            {
                one(info).shouldShowView(method); will(returnValue(false));
            }
        });
        interceptor.intercept(null, this.method, null);
        mockery.assertIsSatisfied();
    }

}
