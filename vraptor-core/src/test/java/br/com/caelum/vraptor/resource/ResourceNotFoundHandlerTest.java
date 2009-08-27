package br.com.caelum.vraptor.resource;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
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
        mockery.checking(new Expectations() {
            {
                one(webResponse).sendError(404);
            }
        });
		notFoundHandler.couldntFind(request);
        mockery.assertIsSatisfied();
	}
}
