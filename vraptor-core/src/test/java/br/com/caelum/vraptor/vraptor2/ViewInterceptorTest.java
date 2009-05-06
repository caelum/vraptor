/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.vraptor2;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewInterceptorTest {

    private VRaptorMockery mockery;
    private MethodInfo requestResult;
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
        this.requestResult = mockery.mock(MethodInfo.class);
        this.result = mockery.mock(PageResult.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.stack = mockery.mock(InterceptorStack.class);
        this.interceptor = new ViewInterceptor(result, requestResult, info);
    }

    @Test
    public void shouldForward() throws SecurityException, InterceptionException, NoSuchMethodException {
        this.method = mockery.methodFor(VRaptor2Component.class, "method");
        mockery.checking(new Expectations() {
            {
            	one(requestResult).getResult(); will(returnValue("ok"));
                one(info).shouldShowView(method); will(returnValue(true));
                one(result).forward("ok");
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
