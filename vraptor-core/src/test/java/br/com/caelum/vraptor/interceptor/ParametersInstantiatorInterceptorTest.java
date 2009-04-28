package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ParametersInstantiatorInterceptorTest {

    private VRaptorMockery mockery;
    private ParametersInstantiatorInterceptor instantiator;
    private MethodParameters params;
    private ParameterNameProvider provider;
    private ParametersProvider parametersProvider;
	private Validator validator;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.params = mockery.mock(MethodParameters.class);
        this.provider = mockery.mock(ParameterNameProvider.class);
        this.parametersProvider = mockery.mock(ParametersProvider.class);
        this.validator = mockery.mock(Validator.class);
        this.instantiator = new ParametersInstantiatorInterceptor(parametersProvider, params, provider, validator);
    }

    class Component {
        void method() {
        }
    }

    @Test
    public void shouldUseTheProvidedParameters() throws InterceptionException, IOException, NoSuchMethodException {
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        final ResourceMethod method = mockery.methodFor(Component.class, "method");
        final Method reflected = method.getMethod();
        mockery.checking(new Expectations() {
            {
                one(parametersProvider).getParametersFor(method, new ArrayList(),null);
                Object[] values = new Object[] { new Object() };
                will(returnValue(values));
                one(provider).parameterNamesFor(reflected);
                will(returnValue(new String[] { "names" }));
                one(stack).next(method, null);
                one(params).set(values, new String[] { "names" });
            }
        });
        instantiator.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

}
