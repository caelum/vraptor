package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ParametersInstantiatorInterceptorTest {

    private VRaptorMockery mockery;
    private ParametersInstantiatorInterceptor instantiator;
    private MethodInfo params;
    private ParameterNameProvider provider;
    private ParametersProvider parametersProvider;
	private Validator validator;
	private Localization localization;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.params = mockery.mock(MethodInfo.class);
        this.provider = mockery.mock(ParameterNameProvider.class);
        this.parametersProvider = mockery.mock(ParametersProvider.class);
        this.validator = mockery.mock(Validator.class);
        this.localization = mockery.localization();
        this.instantiator = new ParametersInstantiatorInterceptor(parametersProvider, params, provider, validator, localization);
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
        final ResourceBundle bundle = localization.getBundle();
        mockery.checking(new Expectations() {
            {
                one(parametersProvider).getParametersFor(method, new ArrayList(), bundle);
                Object[] values = new Object[] { new Object() };
                will(returnValue(values));
                one(provider).parameterNamesFor(reflected);
                will(returnValue(new String[] { "names" }));
                one(stack).next(method, null);
                one(params).setParameters(values, new String[] { "names" });
            }
        });
        instantiator.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

}
