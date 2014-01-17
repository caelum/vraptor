package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.interceptor.FlashInterceptor.FLASH_INCLUDED_PARAMETERS;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.VRaptorResponse;

/**
 * Tests FlashInterceptor
 *
 * @author Lucas Cavalcanti
 * @author Adriano Almeida
 * @author Caires Vinicius
 */
public class FlashInterceptorTest {


	private @Mock HttpSession session;
	private @Mock Result result;
	private @Mock InterceptorStack stack;
	private @Mock HttpServletResponse mockResponse;
	private MutableResponse response;
	private FlashInterceptor interceptor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		response = new VRaptorResponse(mockResponse);
		interceptor = new FlashInterceptor(session, result, response);
	}

	@Test
	public void shouldAcceptAlways() {
		assertTrue(interceptor.accepts(null));
	}
	
	@Test
	public void shouldDoNothingWhenThereIsNoFlashParameters() throws Exception {
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(null);

		interceptor.intercept(stack, null, null);
		verifyZeroInteractions(result);
	}

	@Test
	public void shouldAddAllFlashParametersToResult() throws Exception {
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(singletonMap("Abc", 1002));
		
		interceptor.intercept(stack, null, null);
		verify(result).include("Abc", 1002);
	}

	@Test
	public void shouldRemoveFlashIncludedParameters() throws Exception {
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(singletonMap("Abc", 1002));
		
		interceptor.intercept(stack, null, null);
		verify(session).removeAttribute(FLASH_INCLUDED_PARAMETERS);
	}
	
	@Test
	public void shouldIncludeFlashParametersWhenARedirectHappens() throws Exception {
		Map<String, Object> parameters = Collections.<String, Object>singletonMap("Abc", 1002);

		when(result.included()).thenReturn(parameters);
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(null);

		interceptor.intercept(stack, null, null);
		response.sendRedirect("Anything");
		
		verify(session).setAttribute(FLASH_INCLUDED_PARAMETERS, parameters);
	}
	@Test
	public void shouldNotIncludeFlashParametersWhenThereIsNoIncludedParameter() throws Exception {
		Map<String, Object> parameters = Collections.emptyMap();

		when(result.included()).thenReturn(parameters);
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(null);

		interceptor.intercept(stack, null, null);
		response.sendRedirect("Anything");

		verify(session, never()).setAttribute(anyString(), anyObject());
	}
	
	@Test
	public void shouldNotCrashWhenSessionIsInvalid() throws Exception {
		Map<String, Object> parameters = Collections.<String, Object>singletonMap("Abc", 1002);

		when(result.included()).thenReturn(parameters);
		doThrow(new IllegalStateException()).when(session).setAttribute(FLASH_INCLUDED_PARAMETERS, parameters);
		when(session.getAttribute(FLASH_INCLUDED_PARAMETERS)).thenReturn(null);

		interceptor.intercept(stack, null, null);
		response.sendRedirect("Anything");
	}
}
