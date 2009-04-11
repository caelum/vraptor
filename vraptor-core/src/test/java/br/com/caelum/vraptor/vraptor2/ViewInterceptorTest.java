package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewInterceptorTest {

    private Mockery mockery;
    private RequestResult requestResult;
    private PageResult result;
    private ViewInterceptor interceptor;

    @Before
    public void setup() throws NoSuchMethodException {
        this.mockery = new Mockery();
        this.requestResult = new RequestResult();
        this.requestResult.setValue("ok");
        this.result = mockery.mock(PageResult.class);
        this.interceptor = new ViewInterceptor(result, requestResult);
    }

    @Test
    public void shouldForward() throws SecurityException, NoSuchMethodException, InterceptionException, IOException, ServletException {
        mockery.checking(new Expectations() {
            {
                one(result).forward("ok");
            }
        });
        interceptor.intercept(null, null, null);
        mockery.assertIsSatisfied();
    }


}
