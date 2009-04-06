package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorListPriorToExecutionExtractorTest {

    private VRaptorMockery mockery;
    private InterceptorRegistry registry;
    private InterceptorListPriorToExecutionExtractor extractor;
    private ResourceMethod method;
    private InterceptorStack stack;
    private Container container;

    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        this.mockery = new VRaptorMockery();
        this.container = mockery.mock(Container.class);
        this.registry = mockery.mock(InterceptorRegistry.class);
        this.extractor = new InterceptorListPriorToExecutionExtractor(registry, container);
        this.method = mockery.mock(ResourceMethod.class);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    @Test
    public void shouldAddTheListOfInterceptorsAsFollowingInterceptors() throws InterceptionException, IOException {
        final List<Interceptor> interceptors = new ArrayList<Interceptor>();
        final Interceptor[] array = interceptors.toArray(new Interceptor[interceptors.size()]);
        mockery.checking(new Expectations() {
            {
                one(registry).interceptorsFor(method, container);
                will(returnValue(array));
                for (Interceptor i : array) {
                    one(stack).addAsNext(i);
                }
                one(stack).next(method, null);
            }
        });
        extractor.intercept(this.stack, method, null);
        mockery.assertIsSatisfied();
    }

}
