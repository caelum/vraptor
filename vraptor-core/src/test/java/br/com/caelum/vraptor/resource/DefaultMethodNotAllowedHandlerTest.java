package br.com.caelum.vraptor.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class DefaultMethodNotAllowedHandlerTest {

	private DefaultMethodNotAllowedHandler handler;
	private RequestInfo request;
	private MutableResponse response;
	private MutableRequest mockRequest;

	@Before
	public void setUp() throws Exception {
		this.response = mock(MutableResponse.class);
		this.mockRequest = mock(MutableRequest.class);
		this.request = new RequestInfo(null, null, mockRequest, response);
		this.handler = new DefaultMethodNotAllowedHandler();
	}

	@Test
	public void shouldAddAllowHeader() throws Exception {
		this.handler.deny(request, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).addHeader("Allow", "GET, POST");

	}

	@Test
	public void shouldSendErrorMethodNotAllowed() throws Exception {
		this.handler.deny(request, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	@Test
	public void shouldNotSendMethodNotAllowedIfTheRequestMethodIsOptions() throws Exception {
		when(mockRequest.getMethod()).thenReturn("OPTIONS");

		this.handler.deny(request, EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response, never()).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
