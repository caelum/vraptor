package br.com.caelum.vraptor.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class ResourceNotFoundHandlerTest {

	private ResourceNotFoundHandler notFoundHandler;
	private Mockery mockery;
	private HttpServletResponse webResponse;

	@Before
	public void setUp() {
		this.mockery = new Mockery();
		this.webResponse = mockery.mock(HttpServletResponse.class);
		this.notFoundHandler = new DefaultResourceNotFoundHandler();
	}
	
	@Test
	public void couldntFindWritesResourceNotFoundOnTheResponse() throws Exception {
        final StringWriter writer = new StringWriter();
        mockery.checking(new Expectations() {
            {
                one(webResponse).setStatus(404);
                one(webResponse).getWriter();
                will(returnValue(new PrintWriter(writer)));
            }
        });
		notFoundHandler.couldntFind(webResponse);
        MatcherAssert.assertThat(writer.getBuffer().toString(), Matchers.is(Matchers.equalTo("resource not found\n")));
        mockery.assertIsSatisfied();
	}
}
