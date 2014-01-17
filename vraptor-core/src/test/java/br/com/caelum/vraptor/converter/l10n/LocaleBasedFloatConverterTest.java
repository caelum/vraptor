/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.converter.l10n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

public class LocaleBasedFloatConverterTest {

	static final String LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";
	
	private LocaleBasedFloatConverter converter;
	private @Mock MutableRequest request;
	private @Mock HttpSession session;
	private @Mock ServletContext context;
	private ResourceBundle bundle;
	private JstlLocalization jstlLocalization;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
	FilterChain chain = mock(FilterChain.class);
	final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
	jstlLocalization = new JstlLocalization(webRequest);
	converter = new LocaleBasedFloatConverter(jstlLocalization);
	bundle = ResourceBundle.getBundle("messages");
	Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void shouldBeAbleToConvertWithPTBR() {
	when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn("pt_BR");
	assertThat(converter.convert("10,00", Float.class, bundle), is(equalTo(new Float("10.00"))));
	assertThat(converter.convert("10,01", Float.class, bundle), is(equalTo(new Float("10.01"))));
	}

	@Test
	public void shouldBeAbleToConvertWithENUS() {
	when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn("en_US");
	assertThat(converter.convert("10.00", Float.class, bundle), is(equalTo(new Float("10.00"))));
	assertThat(converter.convert("10.01", Float.class, bundle), is(equalTo(new Float("10.01"))));
	}

	@Test
	public void shouldUseTheDefaultLocale()
	throws ParseException {
		when(request.getSession()).thenReturn(session);
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn(null);
		when(session.getAttribute("javax.servlet.jsp.jstl.fmt.locale.session")). thenReturn(null);
		when(context.getAttribute("javax.servlet.jsp.jstl.fmt.locale.application")). thenReturn(null);
		when(context.getInitParameter("javax.servlet.jsp.jstl.fmt.locale")). thenReturn(null);
		when(request.getLocale()).thenReturn(Locale.getDefault());
		
	DecimalFormat fmt = new DecimalFormat("##0,00");
	fmt.setMinimumFractionDigits(2);

	Float theValue = new Float("10.00");
	String formattedValue = fmt.format(theValue);
	assertThat(converter.convert(formattedValue, Float.class, bundle), is(equalTo(theValue)));
	}

	 @Test
	 public void shouldBeAbleToConvertEmpty() {
	 assertThat(converter.convert("", Float.class, bundle), is(nullValue()));
	 }
	
	 @Test
	 public void shouldBeAbleToConvertNull() {
	 assertThat(converter.convert(null, Float.class, bundle), is(nullValue()));
	 }
	
	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn("pt_br");
	try {
		converter.convert("vr3.9", Float.class, bundle);
		fail("Should throw exception");
	} catch (ConversionError e) {
		assertThat(e.getMessage(), is(equalTo("vr3.9 is not a valid number.")));
	}
	}
}
