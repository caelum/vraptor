package br.com.caelum.vraptor.http;

import static br.com.caelum.vraptor.config.BasicConfiguration.ENCODING;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.VRaptorException;

public class WebXmlEncodingHandlerTest {
	
	private @Mock ServletContext context;
	private @Mock HttpServletRequest request;
	private @Mock HttpServletResponse response;
	
	@Before
	public void setUp() throws Exception {
	MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldSetEncodingToUTF8() throws Exception {
	when(context.getInitParameter(ENCODING)).thenReturn("UTF-8");
	
	new WebXmlEncodingHandler("UTF-8").setEncoding(request, response);
	
	verify(request).setCharacterEncoding("UTF-8");
	verify(response).setCharacterEncoding("UTF-8");
	}

	@Test(expected=VRaptorException.class)
	public void shouldThrowExceptionWhenAnUnsupportedEncodingExceptionOccurs() throws Exception {
	doThrow(new UnsupportedEncodingException()).when(request).setCharacterEncoding(anyString());
	
	new WebXmlEncodingHandler("UTF-8").setEncoding(request, response);
	}
}
