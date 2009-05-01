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

import java.io.IOException;

import javax.servlet.ServletException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.DogAlike;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ExecuteAndViewInterceptorTest {

    private Mockery mockery;
    private MethodInfo info;
    private InterceptorStack stack;

    @Before
    public void setup() throws NoSuchMethodException {
        this.mockery = new Mockery();
        this.info = mockery.mock(MethodInfo.class);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    @Test
    public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
            NoSuchMethodException, IOException, InterceptionException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(info);
        final ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                one(stack).next(method, auau);
                one(info).getParameters(); will(returnValue(new Object[]{}));
            }
        });
        interceptor.intercept(stack, method, auau);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException,
            NoSuchMethodException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(info);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        final RuntimeException exception = new RuntimeException();
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                will(throwException(exception));
                one(info).getParameters(); will(returnValue(new Object[]{}));
            }
        });
        try {
            interceptor.intercept(stack, method, auau);
            Assert.fail();
        } catch (InterceptionException e) {
            MatcherAssert.assertThat((RuntimeException) e.getCause(), Matchers.is(Matchers.equalTo(exception)));
            mockery.assertIsSatisfied();
        }
    }
    
    @Test
    public void shouldUseTheProvidedArguments() throws SecurityException, NoSuchMethodException, InterceptionException, IOException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(info);
        final ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark", int.class));
        final DogAlike auau = mockery.mock(DogAlike.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark(3);
                one(stack).next(method, auau);
                one(info).getParameters(); will(returnValue(new Object[]{3}));
            }
        });
        interceptor.intercept(stack, method, auau);
        mockery.assertIsSatisfied();
    }
    
    @org.vraptor.annotations.Component
    interface OldDog {
        public void bark();
        public String barkResponse();
    }

    @Test
    public void shouldForwardIfUsingAnOldComponent() throws SecurityException, NoSuchMethodException, InterceptionException, IOException, ServletException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(info);
        final ResourceMethod method = new DefaultResourceMethod(new DefaultResource(OldDog.class), OldDog.class.getMethod("bark"));
        final OldDog auau = mockery.mock(OldDog.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                one(stack).next(method, auau);
                one(info).getParameters(); will(returnValue(new Object[]{}));
                one(info).setResult("ok");
            }
        });
        interceptor.intercept(stack, method, auau);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldForwardWithResultIfUsingAnOldComponent() throws SecurityException, NoSuchMethodException, InterceptionException, IOException, ServletException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(info);
        final ResourceMethod method = new DefaultResourceMethod(new DefaultResource(OldDog.class), OldDog.class.getMethod("barkResponse"));
        final OldDog auau = mockery.mock(OldDog.class);
        mockery.checking(new Expectations() {
            {
                one(auau).barkResponse(); will(returnValue("response"));
                one(stack).next(method, auau);
                one(info).getParameters(); will(returnValue(new Object[]{}));
                one(info).setResult("response");
            }
        });
        interceptor.intercept(stack, method, auau);
        mockery.assertIsSatisfied();
    }

}
