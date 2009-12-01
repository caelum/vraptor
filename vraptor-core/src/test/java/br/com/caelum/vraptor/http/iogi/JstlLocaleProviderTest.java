package br.com.caelum.vraptor.http.iogi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.servlet.FilterChain;

import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;


public class JstlLocaleProviderTest {

	@Test
	public void willReturnValueSetAsARequestAttribute() throws Exception {
		final MutableRequest request = mock(MutableRequest.class);
		FilterChain chain = mock(FilterChain.class);
		RequestInfo mockRequestInfo = new RequestInfo(null, chain, request, null);
		JstlLocaleProvider localeProvider = new JstlLocaleProvider(mockRequestInfo);
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn("pt_br");

		Locale locale = localeProvider.getLocale();
		assertEquals("pt", locale.getLanguage());
		assertEquals("BR", locale.getCountry());

		verify(request, times(2)).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
	}
}
