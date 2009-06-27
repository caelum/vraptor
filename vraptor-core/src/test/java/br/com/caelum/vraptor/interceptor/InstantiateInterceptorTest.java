package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DogController;

public class InstantiateInterceptorTest {

    private Mockery mockery;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = mockery.mock(Container.class);
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
                one(method).getResource(); will(returnValue(new DefaultResourceClass(DogController.class)));
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

}
