package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ValidatorTest {

    private VRaptorMockery mockery;
    private Validator validator;
    private PageResult result;
    private ParametersProvider provider;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.result = mockery.mock(PageResult.class);
        this.provider = mockery.mock(ParametersProvider.class);
        this.validator = new Validator(this.provider, this.result);
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
    class MyComponent {
        void noValidation() {
        }
    }


    @Test
    public void doestNothingIfValidationMethodNotFound() throws NoSuchMethodException, InterceptionException, IOException {
        final OldComponent resourceInstance = new OldComponent();
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
    public void forwardToValidationPageWithErrorsIfSomeFound() {
        mockery.assertIsSatisfied();
    }

    @Test
    public void doesNothingIfValidationMethodExistsButNoErrorsOccur() {
        mockery.assertIsSatisfied();
    }

}
