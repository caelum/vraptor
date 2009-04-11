package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class OutjectionInterceptorTest {

    private VRaptorMockery mockery;
    private HttpServletRequest request;
    private OutjectionInterceptor interceptor;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.interceptor = new OutjectionInterceptor(request);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    public interface WithArgsComponent {
        public String withArgs(String s);
    }

    public interface NoReturnComponent {

        public void noReturn();
    }

    @Test
    public void shouldComplainAboutGetterWithArgs() throws SecurityException, NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodForResource(WithArgsComponent.class);
        final WithArgsComponent component = mockery.mock(WithArgsComponent.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, component);
            }
        });
        interceptor.intercept(stack, method, component);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldComplainAboutNotReturningAnything() throws SecurityException, NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodForResource(NoReturnComponent.class);
        final NoReturnComponent component = mockery.mock(NoReturnComponent.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, component);
            }
        });
        interceptor.intercept(stack, method, component);
        mockery.assertIsSatisfied();
    }

}
