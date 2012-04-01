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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

public class LocaleBasedDoubleConverterTest {

    private LocaleBasedDoubleConverter converter;
    private Mockery mockery;
    private MutableRequest request;
    private HttpSession session;
    private ServletContext context;
    private ResourceBundle bundle;
    private JstlLocalization jstlLocalization;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(MutableRequest.class);
        this.session = mockery.mock(HttpSession.class);
        this.context = mockery.mock(ServletContext.class);
        FilterChain chain = mockery.mock(FilterChain.class);
        final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        this.jstlLocalization = new JstlLocalization(webRequest);
        this.converter = new LocaleBasedDoubleConverter(jstlLocalization);
        this.bundle = ResourceBundle.getBundle("messages");
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void shouldBeAbleToConvert() {
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
                will(returnValue("pt_br"));
            }
        });
        assertThat(converter.convert("19,91", Double.class, bundle), is(equalTo(new Double("19.91"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheDefaultLocale()
        throws ParseException {
        mockery.checking(new Expectations() {
            {
                one(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
                will(returnValue(null));
                one(request).getSession();
                will(returnValue(session));
                one(session).getAttribute("javax.servlet.jsp.jstl.fmt.locale.session");
                will(returnValue(null));
                one(context).getAttribute("javax.servlet.jsp.jstl.fmt.locale.application");
                will(returnValue(null));
                one(context).getInitParameter("javax.servlet.jsp.jstl.fmt.locale");
                will(returnValue(null));
                one(request).getLocale();
                will(returnValue(Locale.getDefault()));
            }
        });
        DecimalFormat fmt = new DecimalFormat("##0,00");
        fmt.setMinimumFractionDigits(2);

        Double theValue = new Double("10.00");
        String formattedValue = fmt.format(theValue);
        assertThat(converter.convert(formattedValue, Double.class, bundle), is(equalTo(theValue)));
        mockery.assertIsSatisfied();
    }

     @Test
     public void shouldBeAbleToConvertEmpty() {
         assertThat(converter.convert("", Double.class, bundle), is(nullValue()));
         mockery.assertIsSatisfied();
     }
    
     @Test
     public void shouldBeAbleToConvertNull() {
         assertThat(converter.convert(null, Double.class, bundle), is(nullValue()));
         mockery.assertIsSatisfied();
     }
    
    @Test
    public void shouldThrowExceptionWhenUnableToParse() {
        mockery.checking(new Expectations() {
            {
                exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
                will(returnValue("pt_br"));
            }
        });
        try {
            converter.convert("vr3.9", Double.class, bundle);
        } catch (ConversionError e) {
            assertThat(e.getMessage(), is(equalTo("vr3.9 is not a valid number.")));
        }
    }

}
