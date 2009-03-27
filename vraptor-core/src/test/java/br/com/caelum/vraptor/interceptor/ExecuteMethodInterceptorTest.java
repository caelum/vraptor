package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ExecuteMethodInterceptorTest {

    @Test
    public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
            NoSuchMethodException, IOException, InterceptionException {
        Mockery mockery = new Mockery();
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor();
        ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException, NoSuchMethodException {
        Mockery mockery = new Mockery();
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor();
        ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        final RuntimeException exception = new RuntimeException();
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                will(throwException(exception));
            }
        });
        try {
            interceptor.intercept(null, method, auau);
            Assert.fail();
        } catch (InterceptionException e) {
            MatcherAssert.assertThat((RuntimeException) e.getCause(), Matchers.is(Matchers.equalTo(exception)));
            mockery.assertIsSatisfied();
        }
    }

}
