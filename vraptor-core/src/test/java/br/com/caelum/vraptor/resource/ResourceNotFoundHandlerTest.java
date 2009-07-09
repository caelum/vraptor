package br.com.caelum.vraptor.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

public class ResourceNotFoundHandlerTest {

	private ResourceNotFoundHandler notFoundHandler;
	private Mockery mockery;
    private MutableRequest webRequest;
    private HttpServletResponse webResponse;
    private RequestInfo request;

    @Before
	public void setUp() {
		this.mockery = new Mockery();
        this.webRequest = mockery.mock(MutableRequest.class);
        this.webResponse = mockery.mock(HttpServletResponse.class);
        this.request = new RequestInfo(null, webRequest, webResponse);
		this.notFoundHandler = new DefaultResourceNotFoundHandler();
	}
	
	@Test
	public void couldntFindWritesResourceNotFoundOnTheResponse() throws Exception {
        final StringWriter writer = new StringWriter();
        mockery.checking(new Expectations() {
            {
                one(webRequest).getRequestURI();
                will(returnValue("/some/requested/component"));
                one(webResponse).sendError(404);
                one(webResponse).getWriter();
                will(returnValue(new PrintWriter(writer)));
            }
        });
		notFoundHandler.couldntFind(request);
		Assert.assertTrue(writer.getBuffer().toString().contains("Nothing at URI: /some/requested/component"));
        mockery.assertIsSatisfied();
	}
}
