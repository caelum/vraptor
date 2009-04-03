package br.com.caelum.vraptor.core;

import java.io.IOException;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ToInstantiateInterceptorHandlerTest {

    private VRaptorMockery mockery;
    private ToInstantiateInterceptorHandler handler;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.container = mockery.container(MyInterceptor.class, null);
        this.handler = new ToInstantiateInterceptorHandler(container, MyInterceptor.class);
    }

    public static class MyInterceptor implements Interceptor {
        public MyInterceptor(Dependency d) {
        }

        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws IOException, InterceptionException {
        }
    }

    public static class Dependency {

    }

    @Test(expected = InterceptionException.class)
    public void shouldComplainWhenUnableToInstantiateAnInterceptor() throws InterceptionException, IOException {
        handler.execute(null, null, null);
    }

}
