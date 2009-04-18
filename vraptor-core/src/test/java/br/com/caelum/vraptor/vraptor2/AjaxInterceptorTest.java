package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.vraptor2.outject.JsonExporter;

public class AjaxInterceptorTest {

    private Mockery mockery;
    private ComponentInfoProvider info;
    private HttpServletResponse response;
    private JsonExporter outjecter;
    private AjaxInterceptor interceptor;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.stack = mockery.mock(InterceptorStack.class);
        this.outjecter = new JsonExporter();
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.interceptor = new AjaxInterceptor(outjecter, response, info);
    }

    @Test
    public void invokesNextIfNonAjax() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                one(info).isAjax(); will(returnValue(false));
                one(stack).next(null, null);
            }
        });
        interceptor.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void outjectsToResponseIfAjax() throws InterceptionException, IOException {
        outjecter.include("author", "Guilherme");
        StringWriter content = new StringWriter();
        final PrintWriter writer = new PrintWriter(content);
        mockery.checking(new Expectations() {
            {
                one(info).isAjax(); will(returnValue(true));
                one(response).setContentType("application/json");
                one(response).setCharacterEncoding("UTF-8");
                one(response).getWriter(); will(returnValue(writer));
            }
        });
        interceptor.intercept(stack, null, null);
        assertThat(content.getBuffer().toString(), is(equalTo("{\"author\":\"Guilherme\"}")));
        mockery.assertIsSatisfied();
    }

}
