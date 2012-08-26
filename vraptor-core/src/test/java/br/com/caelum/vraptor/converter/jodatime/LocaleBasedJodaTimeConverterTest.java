package br.com.caelum.vraptor.converter.jodatime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Tests to {@link LocaleBasedJodaTimeConverter}.
 */
public class LocaleBasedJodaTimeConverterTest {

	static final String LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";
	
	private LocaleBasedJodaTimeConverter converter;
	private @Mock MutableRequest request;
	private @Mock HttpSession session;
	private @Mock ServletContext context;
	private @Mock JstlLocalization jstlLocalization;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		FilterChain chain = mock(FilterChain.class);
		final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        jstlLocalization = new JstlLocalization(webRequest);
		
		converter = new LocaleBasedJodaTimeConverter(jstlLocalization);
	}

	@Test
	public void shouldUseTheDefaultLocale() throws ParseException {
		when(request.getAttribute(LOCALE_KEY + ".request")).thenReturn(null);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(LOCALE_KEY + ".session")). thenReturn(null);
		when(context.getAttribute(LOCALE_KEY + ".application")). thenReturn(null);
		when(context.getInitParameter(LOCALE_KEY)). thenReturn(null);
		when(request.getLocale()).thenReturn(new Locale("pt_BR"));
		
		assertThat(new Locale("pt_BR"), is(equalTo(converter.getLocale())));
	}
	
	@Test
	public void shouldUseTheDefaulJvmtLocale() throws ParseException {
		when(request.getAttribute(LOCALE_KEY + ".request")).thenReturn(null);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(LOCALE_KEY + ".session")). thenReturn(null);
		when(context.getAttribute(LOCALE_KEY + ".application")). thenReturn(null);
		when(context.getInitParameter(LOCALE_KEY)). thenReturn(null);
		when(request.getLocale()).thenReturn(null);
		
		assertThat(Locale.getDefault(), is(equalTo(converter.getLocale())));
	}
}