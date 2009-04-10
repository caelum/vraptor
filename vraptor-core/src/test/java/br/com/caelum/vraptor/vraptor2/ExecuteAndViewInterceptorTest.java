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
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.interceptor.DogAlike;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ExecuteAndViewInterceptorTest {

    private Mockery mockery;
    private ParametersProvider provider;
    private PageResult result;

    @Before
    public void setup() throws NoSuchMethodException {
        this.mockery = new Mockery();
        this.provider = mockery.mock(ParametersProvider.class);
        this.result = mockery.mock(PageResult.class);
    }

    @Test
    public void shouldInvokeTheMethodAndNotProceedWithInterceptorStack() throws SecurityException,
            NoSuchMethodException, IOException, InterceptionException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(provider, result);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                one(provider).getParametersFor(with(VRaptorMatchers.resourceMethod(DogAlike.class.getMethod("bark"))));
                will(returnValue(new Object[]{}));
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThrowMethodExceptionIfThereIsAnInvocationException() throws IOException, SecurityException,
            NoSuchMethodException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(provider, result);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark"));
        final DogAlike auau = mockery.mock(DogAlike.class);
        final RuntimeException exception = new RuntimeException();
        mockery.checking(new Expectations() {
            {
                one(provider).getParametersFor(with(VRaptorMatchers.resourceMethod(DogAlike.class.getMethod("bark"))));
                will(returnValue(new Object[]{}));
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
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(provider, result);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(DogAlike.class), DogAlike.class.getMethod("bark", int.class));
        final DogAlike auau = mockery.mock(DogAlike.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark(3);
                one(provider).getParametersFor(with(VRaptorMatchers.resourceMethod(DogAlike.class.getMethod("bark", int.class))));
                will(returnValue(new Object[]{3}));
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }
    
    @org.vraptor.annotations.Component
    interface OldDog {
        public void bark();
        public String barkResponse();
    }

    @Test
    public void shouldForwardIfUsingAnOldComponent() throws SecurityException, NoSuchMethodException, InterceptionException, IOException, ServletException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(provider, result);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(OldDog.class), OldDog.class.getMethod("bark"));
        final OldDog auau = mockery.mock(OldDog.class);
        mockery.checking(new Expectations() {
            {
                one(auau).bark();
                one(provider).getParametersFor(with(VRaptorMatchers.resourceMethod(OldDog.class.getMethod("bark"))));
                will(returnValue(new Object[0]));
                one(result).forward("ok");
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldForwardWithResultIfUsingAnOldComponent() throws SecurityException, NoSuchMethodException, InterceptionException, IOException, ServletException {
        ExecuteAndViewInterceptor interceptor = new ExecuteAndViewInterceptor(provider, result);
        ResourceMethod method = new DefaultResourceMethod(new DefaultResource(OldDog.class), OldDog.class.getMethod("barkResponse"));
        final OldDog auau = mockery.mock(OldDog.class);
        mockery.checking(new Expectations() {
            {
                one(auau).barkResponse(); will(returnValue("response"));
                one(provider).getParametersFor(with(VRaptorMatchers.resourceMethod(OldDog.class.getMethod("barkResponse"))));
                will(returnValue(new Object[0]));
                one(result).forward("response");
            }
        });
        interceptor.intercept(null, method, auau);
        mockery.assertIsSatisfied();
    }

}
