package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.route.Router;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private Router router;
    private HttpServletResponse response;
    private ServletContext context;
    private HttpServletRequest request;

    public static class MyComponent {
        public void base() {
        }
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.router = mockery.mock(Router.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.context = mockery.mock(ServletContext.class);
        this.logicResult = new DefaultLogicResult(response, context, request,router);
    }

    @Test
    public void instantiatesUsingTheContainerAndAddsTheExecutionInterceptors() throws NoSuchMethodException, IOException, ServletException {
        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(router).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(request).getRequestDispatcher(url);
                RequestDispatcher dispatcher = mockery.mock(RequestDispatcher.class);
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        logicResult.redirectServerTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {
        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(context).getContextPath(); will(returnValue("/context"));
                one(router).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(response).sendRedirect("/context" + url);
            }
        });
        logicResult.redirectClientTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }
    
}
