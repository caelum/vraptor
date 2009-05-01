package br.com.caelum.vraptor.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorRegistryTest {

    private Mockery mockery;
    private DefaultInterceptorRegistry registry;
    private ResourceMethod method;
    private List<Class<? extends Interceptor>> interceptors;
    private Container container;
    private Interceptor interceptor;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = mockery.mock(Container.class);
        this.method = mockery.mock(ResourceMethod.class);
        this.registry = new DefaultInterceptorRegistry();
        this.interceptors = new ArrayList<Class<? extends Interceptor>>();
        this.interceptors.add(CustomInterceptor.class);
        this.interceptor = mockery.mock(Interceptor.class);
    }

    public static class CustomInterceptor implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    @Test
    public void shouldReturnAnInterceptorWhichAcceptsTheGivenResource() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(CustomInterceptor.class); will(returnValue(interceptor));
                one(interceptor).accepts(method); will(returnValue(true));
            }
        });
        registry.register(interceptors);
        Interceptor[] types = registry.interceptorsFor(method, container);
        assertThat(types.length, is(1));
        assertThat(types[0], is(equalTo(interceptor)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldNotReturnAnInterceptorWhichDoesNotAcceptTheGivenResource() {
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(CustomInterceptor.class); will(returnValue(interceptor));
                one(interceptor).accepts(method); will(returnValue(false));
            }
        });
        registry.register(interceptors);
        Interceptor[] types = registry.interceptorsFor(method, container);
        assertThat(types.length, is(0));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldReturnNoInterceptorIfThereIsNoneRegistered() {
        Interceptor[] types = registry.interceptorsFor(method, container);
        assertThat(types.length, is(0));
        mockery.assertIsSatisfied();
    }

}
