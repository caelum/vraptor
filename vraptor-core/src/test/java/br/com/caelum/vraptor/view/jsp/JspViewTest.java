package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class JspViewTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        request = mockery.mock(HttpServletRequest.class);
        response = mockery.mock(HttpServletResponse.class);
        dispatcher = mockery.mock(RequestDispatcher.class);
    }

    @Test
    public void shouldForwardRequest() throws ServletException, IOException {
        JspView view = new JspView(request, response);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request,response);
            }
        });
        view.forward();
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldIncludeRequest() throws ServletException, IOException {
        JspView view = new JspView(request, response);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("");
                will(returnValue(dispatcher));
                one(dispatcher).include(request,response);
            }
        });
        view.include();
        mockery.assertIsSatisfied();
    }
}
