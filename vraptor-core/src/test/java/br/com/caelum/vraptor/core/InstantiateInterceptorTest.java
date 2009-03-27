package br.com.caelum.vraptor.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InstantiateInterceptorTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.container = mockery.mock(Container.class);
    }

    public static class DogController {

    }

    @Test
    public void shouldUseContainerForNewComponent() throws InterceptionException, IOException {
        InstantiateInterceptor interceptor = new InstantiateInterceptor(container);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        final DogController myDog = new DogController();
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(DogController.class);
                will(returnValue(myDog));
                one(stack).next(method, myDog);
                one(method).getResource(); will(returnValue(new DefaultResource(DogController.class)));
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

}
