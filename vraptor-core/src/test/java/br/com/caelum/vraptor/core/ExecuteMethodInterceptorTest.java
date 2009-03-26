package br.com.caelum.vraptor.core;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ExecuteMethodInterceptorTest {

    @Test
    public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
            NoSuchMethodException, IOException, InterceptionException {
        Mockery mockery = new Mockery();
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor();
        ResourceMethod method = new DefaultResourceMethod(null, Dog.class.getMethod("bark"));
        final Dog auau = mockery.mock(Dog.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }

    public interface Dog {
        abstract void bark();
    }

}
