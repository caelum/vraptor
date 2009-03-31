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
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorListPriorToExecutionExtractorTest {

    private VRaptorMockery mockery;
    private InterceptorRegistry registry;
    private InterceptorListPriorToExecutionExtractor extractor;
    private ResourceMethod method;
    private InterceptorStack stack;

    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        this.mockery = new VRaptorMockery();
        this.registry = mockery.mock(InterceptorRegistry.class);
        this.extractor = new InterceptorListPriorToExecutionExtractor(registry);
        this.method = mockery.mock(ResourceMethod.class);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddTheListOfInterceptorsAsFollowingInterceptors() throws InterceptionException, IOException {
        final List<Class<? extends Interceptor>> interceptors = new ArrayList<Class<? extends Interceptor>>();
        final Class[] array = interceptors.toArray(new Class[interceptors.size()]);
        mockery.checking(new Expectations() {
            {
                one(registry).interceptorsFor(method); will(returnValue(array));
                one(stack).addAsNext(array);
                one(stack).next(method, null);
            }
        });
        extractor.intercept(this.stack, method, null);
        mockery.assertIsSatisfied();
    }
    
}
