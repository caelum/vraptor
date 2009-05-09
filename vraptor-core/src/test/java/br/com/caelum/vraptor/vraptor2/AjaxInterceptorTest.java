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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Remotable;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;

public class AjaxInterceptorTest {

    private VRaptorMockery mockery;
    private ComponentInfoProvider info;
    private HttpServletResponse response;
    private JsonOutjecter outjecter;
    private AjaxInterceptor interceptor;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.stack = mockery.mock(InterceptorStack.class);
        this.outjecter = new JsonOutjecter();
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.response = mockery.mock(HttpServletResponse.class);
        mockery.checking(new Expectations() {
            {
                allowing(info).getOutjecter(); will(returnValue(outjecter));
            }
        });
        this.interceptor = new AjaxInterceptor(response, info);
    }

    @Test
    public void invokesNextIfNonAjax() throws InterceptionException, IOException, NoSuchMethodException {
    	final ResourceMethod method = mockery.methodFor(MyComponent.class, "nonAjaxed");
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(false));
                one(stack).next(method, null);
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

    class MyComponent {
        @Remotable
        void ajaxed() {
        }

        void nonAjaxed() {
        }
    }

    @Test
    public void outjectsToResponseIfAjax() throws InterceptionException, IOException, NoSuchMethodException {
        outjecter.include("author", "Guilherme");
        StringWriter content = new StringWriter();
        final PrintWriter writer = new PrintWriter(content);
        final ResourceMethod method = mockery.methodFor(MyComponent.class, "ajaxed");
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(true));
                one(response).setContentType("application/json");
                one(response).setCharacterEncoding("UTF-8");
                one(response).getWriter();
                will(returnValue(writer));
            }
        });
        interceptor.intercept(stack, method, null);
        assertThat(content.getBuffer().toString(), is(equalTo("{\"author\":\"Guilherme\"}")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void complainsIfTryingToAjaxANonRemotableMethod() throws IOException, NoSuchMethodException {
        outjecter.include("author", "Guilherme");
        final ResourceMethod method = mockery.methodFor(MyComponent.class, "nonAjaxed");
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(true));
            }
        });
        try {
            interceptor.intercept(stack, method, null);
            Assert.fail();
        } catch (InterceptionException e) {
            // DO NOT move it from here... we need to satisfy the mockery issues
            mockery.assertIsSatisfied();
        }
    }

}
