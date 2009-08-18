package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.NotNull;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.plugin.hibernate.Validate;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class HibernateValidatorPluginInterceptorTest {

    private VRaptorMockery mockery;
    private MethodInfo parameters;
    private HttpServletRequest request;
    private ParameterNameProvider provider;
    private ValidationErrors errors;
    private HibernateValidatorPluginInterceptor interceptor;
    private InterceptorStack stack;
	private Localization localization;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.localization = mockery.localization();
        this.interceptor = new HibernateValidatorPluginInterceptor(errors, provider, request, parameters, localization);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    class Door {
        @NotNull
        String color;
    }
    class Window {
    }
    class Car {
        void paintWithoutValidate(Door door) {
        }
        @Validate
        void paint(Window w) {
        }
    }

    @Test
    public void shouldDoNothingIfTheMethodShouldNotBeValidatedButTheParamIsUnvalid() throws NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodFor(Car.class, "paintWithoutValidate", Door.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, null);
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();

    }

    @Test
    public void shouldDoNothingIfTheMethodAsksForValidationOfNoParams() throws NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodFor(Car.class, "paintWithoutValidate", Door.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, null);
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();

    }

}
