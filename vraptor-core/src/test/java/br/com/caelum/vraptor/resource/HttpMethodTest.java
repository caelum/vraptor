package br.com.caelum.vraptor.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HttpMethodTest {

	private @Mock HttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldConvertGETStringToGetMethodForRequestParameter() throws Exception {
		when(request.getParameter("_method")).thenReturn("gEt");
		when(request.getMethod()).thenReturn("POST");
		
		assertEquals(HttpMethod.GET, HttpMethod.of(request));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNotKnowHttpMethodsForRequestParameter() throws Exception {
		when(request.getParameter("_method")).thenReturn("JUMP!");
		when(request.getMethod()).thenReturn("POST");
				
		HttpMethod.of(request);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIf_methodIsUsedInGETRequests() throws Exception {
		when(request.getParameter("_method")).thenReturn("DELETE");
		when(request.getMethod()).thenReturn("GET");
				
		HttpMethod.of(request);
	}

	@Test
	public void shouldConvertGETStringToGetMethod() throws Exception {
		when(request.getParameter("_method")).thenReturn(null);
		when(request.getMethod()).thenReturn("gEt");
		
		assertEquals(HttpMethod.GET, HttpMethod.of(request));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNotKnowHttpMethods() throws Exception {
		when(request.getParameter("_method")).thenReturn(null);
		when(request.getMethod()).thenReturn("JUMP!");
		
		HttpMethod.of(request);
	}

	@Test
	public void shouldUseParameterNameBeforeTryingHttpRealMethod() throws Exception {
		when(request.getMethod()).thenReturn("dElEtE");
		when(request.getParameter("_method")).thenReturn("post");
		
		assertEquals(HttpMethod.POST, HttpMethod.of(request));
	}
}
