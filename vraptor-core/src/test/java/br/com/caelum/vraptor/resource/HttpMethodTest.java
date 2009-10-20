package br.com.caelum.vraptor.resource;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class HttpMethodTest {

	private Mockery mockery;
	private HttpServletRequest request;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.request = mockery.mock(HttpServletRequest.class);
	}

	@Test
	public void shouldConvertGETStringToGetMethodForRequestParameter() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getParameter("_method");
				will(returnValue("gEt"));
			}
		});
		assertEquals(HttpMethod.GET, HttpMethod.of(request));

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNotKnowHttpMethodsForRequestParameter() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getParameter("_method");
				will(returnValue("JUMP!"));
			}
		});
		HttpMethod.of(request);

	}

	@Test
	public void shouldConvertGETStringToGetMethod() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getParameter("_method");
				will(returnValue(null));

				one(request).getMethod();
				will(returnValue("gEt"));
			}
		});
		assertEquals(HttpMethod.GET, HttpMethod.of(request));

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNotKnowHttpMethods() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getParameter("_method");
				will(returnValue(null));

				one(request).getMethod();
				will(returnValue("JUMP!"));
			}
		});
		HttpMethod.of(request);

	}

	@Test
	public void shouldUseParameterNameBeforeTryingHttpRealMethod() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getMethod();
				will(returnValue("gEt"));

				one(request).getParameter("_method");
				will(returnValue("post"));
			}
		});
		assertEquals(HttpMethod.POST, HttpMethod.of(request));

	}

}
