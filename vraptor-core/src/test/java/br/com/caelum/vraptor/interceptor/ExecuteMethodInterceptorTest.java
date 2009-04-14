package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ExecuteMethodInterceptorTest {

    private Mockery mockery;
    private ParametersProvider provider;
    private MethodParameters parameters;

    @Before
    public void setup() throws NoSuchMethodException {
        this.mockery = new Mockery();
        this.provider = mockery.mock(ParametersProvider.class);
        this.parameters =new MethodParameters();
    }

    @Test
    public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
            NoSuchMethodException, IOException, InterceptionException {
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor(provider, parameters);
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
    public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException,
            NoSuchMethodException {
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor(provider, parameters);
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
    
    @Test
    public void shouldUseTheProvidedArguments() throws SecurityException, NoSuchMethodException, InterceptionException, IOException {
        ExecuteMethodInterceptor interceptor = new ExecuteMethodInterceptor(provider, parameters);
        ResourceMethod method = new DefaultResourceMethod(null, DogAlike.class.getMethod("bark", int.class));
        final DogAlike auau = mockery.mock(DogAlike.class);
        parameters.set(new Object[]{3}, new String[]{"Value"});
        mockery.checking(new Expectations() {
            {
                one(auau).bark(3);
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }

}
