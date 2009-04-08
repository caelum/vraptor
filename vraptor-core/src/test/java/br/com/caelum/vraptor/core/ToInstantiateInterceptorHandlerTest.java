package br.com.caelum.vraptor.core;

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ToInstantiateInterceptorHandlerTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    public static class MyWeirdInterceptor implements Interceptor {
        public MyWeirdInterceptor(Dependency d) {
        }

        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws IOException, InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    public static class Dependency {

    }

    @Test(expected = InterceptionException.class)
    public void shouldComplainWhenUnableToInstantiateAnInterceptor() throws InterceptionException, IOException {
        Container container = mockery.container(MyWeirdInterceptor.class, null);
        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container,
                MyWeirdInterceptor.class);
        handler.execute(null, null, null);
    }

    @Test
    public void shouldInvokeInterceptorsMethodIfAbleToInstantiateIt() throws InterceptionException, IOException {
        final Interceptor interceptor = mockery.mock(Interceptor.class);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final Object instance = new Object();
        Container container = mockery.container(Interceptor.class, interceptor);
        mockery.checking(new Expectations() {
            {
                one(interceptor).intercept(stack, method, instance);
            }
        });
        ToInstantiateInterceptorHandler handler = new ToInstantiateInterceptorHandler(container, Interceptor.class);
        handler.execute(stack, method, instance);
    }

}
