package br.com.caelum.vraptor.converter.jodatime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Tests to {@link LocalTimeConverter}.
 */
public class LocalTimeConverterTest {

	private ResourceBundle bundle;
	private LocalTimeConverter converter;
	private @Mock MutableRequest request;
	private @Mock ServletContext context;
	private @Mock JstlLocalization jstlLocalization;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		FilterChain chain = mock(FilterChain.class);
		final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        jstlLocalization = new JstlLocalization(webRequest);

		converter = new LocalTimeConverter(jstlLocalization);
		bundle = ResourceBundle.getBundle("messages");
	}

	@Test
	public void shouldBeAbleToConvert() {
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"))
			.thenReturn("pt_br");

		assertThat(converter.convert("15:38", LocalTime.class, bundle),
				is(equalTo(new LocalTime(15, 38))));
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", LocalTime.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, LocalTime.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"))
			.thenReturn("pt_br");
		
		try {
			converter.convert("xx:yy:ff", LocalTime.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("xx:yy:ff is not a valid time.")));
		}
	}
}