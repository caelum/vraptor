package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.i18n.Message;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

public class ValidatorInterceptorTest {

    private VRaptorMockery mockery;
    private ValidatorInterceptor validator;
    private PageResult result;
    private ParametersProvider provider;
    private InterceptorStack stack;
    private ValidationErrors errors;
    private Outjecter outjecter;
    private OutjectionInterceptor interceptor;
    private ComponentInfoProvider info;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.result = mockery.mock(PageResult.class);
        this.provider = mockery.mock(ParametersProvider.class);
        this.errors = mockery.mock(ValidationErrors.class);
        this.outjecter = mockery.mock(Outjecter.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        mockery.checking(new Expectations() {
            {
                one(info).getOutjecter(); will(returnValue(outjecter));
            }
        });
        this.interceptor = new OutjectionInterceptor(info);
        this.validator = new ValidatorInterceptor(this.provider, this.result, errors, interceptor);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    class OldComponent {
        void method() {
        }
    }

    @Test
    public void doesNothingIfNotAnOldComponent() throws InterceptionException, IOException, NoSuchMethodException {
        final OldComponent resourceInstance = new OldComponent();
        final ResourceMethod method = mockery.methodFor(OldComponent.class, "method");
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, resourceInstance);
            }
        });
        validator.intercept(stack, method, resourceInstance);
        mockery.assertIsSatisfied();
    }

    @org.vraptor.annotations.Component
    interface MyComponent {
        void noValidation();

        void withValidation();

        void validateWithValidation(ValidationErrors errors);
    }

    @Test
    public void doestNothingIfValidationMethodNotFound() throws NoSuchMethodException, InterceptionException,
            IOException {
        final MyComponent resourceInstance = mockery.mock(MyComponent.class);
        final ResourceMethod method = mockery.methodFor(MyComponent.class, "noValidation");
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, resourceInstance);
            }
        });
        validator.intercept(stack, method, resourceInstance);
        mockery.assertIsSatisfied();
    }

    @Test
    public void forwardToValidationPageWithErrorsIfSomeFound() throws NoSuchMethodException, InterceptionException,
            IOException, ServletException {
        final Message message = new Message("", "");
        final MyComponent resourceInstance = new MyComponent() {
            public void validateWithValidation(ValidationErrors errors) {
                errors.add(message);
            }

            public void noValidation() {
            }

            public void withValidation() {
            }
        };
        final ResourceMethod method = mockery.methodFor(MyComponent.class, "withValidation");
        mockery.checking(new Expectations() {
            {
                one(provider).getParametersFor(method);
                will(returnValue(new Object[0]));
                one(result).include(with(equal("errors")), with(an(ValidationErrors.class)));
                one(result).forward("invalid");
                one(errors).add(message);
                one(errors).size();
                will(returnValue(1));
            }
        });
        validator.intercept(stack, method, resourceInstance);
        mockery.assertIsSatisfied();
    }

    @Test
    public void doesNothingIfValidationMethodExistsButNoErrorsOccur() throws NoSuchMethodException,
            InterceptionException, IOException {
        final MyComponent resourceInstance = new MyComponent() {
            public void validateWithValidation(ValidationErrors errors) {
            }

            public void noValidation() {
            }

            public void withValidation() {
            }
        };
        final ResourceMethod method = mockery.methodFor(MyComponent.class, "withValidation");
        mockery.checking(new Expectations() {
            {
                one(provider).getParametersFor(method);
                will(returnValue(new Object[0]));
                one(stack).next(method, resourceInstance);
                one(errors).size();
                will(returnValue(0));
            }
        });
        validator.intercept(stack, method, resourceInstance);
        mockery.assertIsSatisfied();
    }

}
