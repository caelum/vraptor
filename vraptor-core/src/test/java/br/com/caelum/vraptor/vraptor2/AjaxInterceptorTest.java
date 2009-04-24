package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Remotable;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;

public class AjaxInterceptorTest {

    private VRaptorMockery mockery;
    private ComponentInfoProvider info;
    private HttpServletResponse response;
    private JsonOutjecter outjecter;
    private AjaxInterceptor interceptor;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.stack = mockery.mock(InterceptorStack.class);
        this.outjecter = new JsonOutjecter();
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.response = mockery.mock(HttpServletResponse.class);
        mockery.checking(new Expectations() {
            {
                allowing(info).getOutjecter(); will(returnValue(outjecter));
            }
        });
        this.interceptor = new AjaxInterceptor(response, info);
    }

    @Test
    public void invokesNextIfNonAjax() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(false));
                one(stack).next(null, null);
            }
        });
        interceptor.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

    class MyComponent {
        @Remotable
        void ajaxed() {
        }

        void nonAjaxed() {
        }
    }

    @Test
    public void outjectsToResponseIfAjax() throws InterceptionException, IOException, NoSuchMethodException {
        outjecter.include("author", "Guilherme");
        StringWriter content = new StringWriter();
        final PrintWriter writer = new PrintWriter(content);
        ResourceMethod method = mockery.methodFor(MyComponent.class, "ajaxed");
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(true));
                one(response).setContentType("application/json");
                one(response).setCharacterEncoding("UTF-8");
                one(response).getWriter();
                will(returnValue(writer));
            }
        });
        interceptor.intercept(stack, method, null);
        assertThat(content.getBuffer().toString(), is(equalTo("{\"author\":\"Guilherme\"}")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void complainsIfTryingToAjaxANonRemotableMethod() throws IOException, NoSuchMethodException {
        outjecter.include("author", "Guilherme");
        ResourceMethod method = mockery.methodFor(MyComponent.class, "nonAjaxed");
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(true));
            }
        });
        try {
            interceptor.intercept(stack, method, null);
            Assert.fail();
        } catch (InterceptionException e) {
            // DO NOT move it from here... we need to satisfy the mockery issues
            mockery.assertIsSatisfied();
        }
    }

}
