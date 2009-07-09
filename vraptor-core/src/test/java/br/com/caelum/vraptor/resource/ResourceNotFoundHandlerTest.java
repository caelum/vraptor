package br.com.caelum.vraptor.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;

public class ResourceNotFoundHandlerTest {

	private ResourceNotFoundHandler notFoundHandler;
	private Mockery mockery;
	private HttpServletResponse webResponse;
	private RequestInfo request;

	@Before
	public void setUp() {
		this.mockery = new Mockery();
		this.webResponse = mockery.mock(HttpServletResponse.class);
		this.request = new RequestInfo(null, null, webResponse);
		this.notFoundHandler = new DefaultResourceNotFoundHandler();
	}
	
	@Test
	public void couldntFindWritesResourceNotFoundOnTheResponse() throws Exception {
        final StringWriter writer = new StringWriter();
        mockery.checking(new Expectations() {
            {
                one(webResponse).sendError(404);
                one(webResponse).getWriter();
                will(returnValue(new PrintWriter(writer)));
            }
        });
		notFoundHandler.couldntFind(request);
		Assert.assertTrue(writer.getBuffer().toString().contains("resource not found"));
        mockery.assertIsSatisfied();
	}
}
