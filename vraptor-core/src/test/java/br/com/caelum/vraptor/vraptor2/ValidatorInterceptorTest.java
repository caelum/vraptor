package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.ValidationErrors;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
    private Localization localization;
    private ArrayList<Message> listErrors;
    private ResourceBundle bundle;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.result = mockery.mock(PageResult.class);
        this.provider = mockery.mock(ParametersProvider.class);
        this.errors = mockery.mock(ValidationErrors.class);
        this.outjecter = mockery.mock(Outjecter.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.localization = mockery.mock(Localization.class);
        this.listErrors = new ArrayList<Message>();
        this.bundle = ResourceBundle.getBundle("messages");
        mockery.checking(new Expectations() {
            {
                one(info).getOutjecter();
                will(returnValue(outjecter));
                allowing(localization).getBundle();
                will(returnValue(bundle));
            }
        });
        this.interceptor = new OutjectionInterceptor(info);
        this.validator = new ValidatorInterceptor(this.provider, this.result, errors, interceptor, localization);
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
                one(errors).size();
                will(returnValue(0));
                one(stack).next(method, resourceInstance);
            }
        });
        validator.intercept(stack, method, resourceInstance);
        mockery.assertIsSatisfied();
    }

    @Test
    public void forwardToValidationPageWithErrorsIfSomeFound() throws NoSuchMethodException, InterceptionException,
            IOException, ServletException {
        final org.vraptor.i18n.Message message = new org.vraptor.i18n.Message("", "is_not_a_valid_number");
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
                one(provider).getParametersFor(method, listErrors, bundle);
                will(returnValue(new Object[0]));
                one(result).include(with(equal("errors")), with(an(ValidationErrors.class)));
                one(result).forward("invalid");
                one(errors).add(with(fixedMessage("invalid")));
                one(errors).size();
                will(returnValue(1));
                one(localization).getMessage("is_not_a_valid_number");
                will(returnValue("invalid"));
            }

            private TypeSafeMatcher<FixedMessage> fixedMessage(final String key) {
                return new TypeSafeMatcher<FixedMessage>() {

                    protected void describeMismatchSafely(FixedMessage item, Description mismatchDescription) {
                        mismatchDescription.appendText(item.getKey());
                    }

                    protected boolean matchesSafely(FixedMessage item) {
                        return item.getKey().equals(key);
                    }

                    public void describeTo(Description description) {
                        description.appendText(key);
                    }

                };
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
                one(provider).getParametersFor(method, listErrors, bundle);
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
