package br.com.caelum.vraptor.resource;

import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultMethodNotAllowedHandlerTest {

	private VRaptorMockery mockery;
	private DefaultMethodNotAllowedHandler handler;
	private RequestInfo request;
	private MutableResponse response;

	@Before
	public void setUp() throws Exception {
		this.mockery = new VRaptorMockery();
		this.handler = new DefaultMethodNotAllowedHandler();
		this.response = mockery.mock(MutableResponse.class);
		this.request = new RequestInfo(null, null, null, response);
	}

	@Test
	public void shouldAddAllowHeader() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(response).addHeader("Allow", "GET, POST");
				ignoring(anything());
			}
		});

		this.handler.deny(request, EnumSet.of(HttpMethod.GET, HttpMethod.POST));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldSendErrorMethodNotAllowed() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				ignoring(anything());
			}
		});

		this.handler.deny(request, EnumSet.of(HttpMethod.GET, HttpMethod.POST));
		mockery.assertIsSatisfied();
	}

}
