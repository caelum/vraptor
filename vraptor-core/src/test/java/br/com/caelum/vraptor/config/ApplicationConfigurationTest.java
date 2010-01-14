package br.com.caelum.vraptor.config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ApplicationConfigurationTest {

	@Mock
	HttpServletRequest request;
	private ApplicationConfiguration configuration;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.configuration = new ApplicationConfiguration(request);
	}

	@Test
	public void shouldNotUsePortWhenPortIs80() {
		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("caelum.com.br");
		when(request.getServerPort()).thenReturn(80);
		when(request.getContextPath()).thenReturn("/context/");

		assertEquals("http://caelum.com.br/context/", configuration.getApplicationPath());
	}

	@Test
	public void shouldGiveFullUrlWithPortWhenPortIsNot80() {
		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("caelum.com.br");
		when(request.getServerPort()).thenReturn(8080);
		when(request.getContextPath()).thenReturn("/context/");

		assertEquals("http://caelum.com.br:8080/context/", configuration.getApplicationPath());
	}

}
