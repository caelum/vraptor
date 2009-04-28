/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.validator.ValidationMessage;

public class LocaleBasedCalendarConverterTest {

    private LocaleBasedCalendarConverter converter;
    private Mockery mockery;
    private HttpServletRequest request;
    private HttpSession session;
    private ServletContext context;
	private ArrayList<ValidationMessage> errors;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.mockery =new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.session = mockery.mock(HttpSession.class);
        this.context= mockery.mock(ServletContext.class);
        final VRaptorRequest webRequest = new VRaptorRequest(context, request, null);
        this.converter = new LocaleBasedCalendarConverter(webRequest);
        this.errors = new ArrayList<ValidationMessage>();
        this.bundle = ResourceBundle.getBundle("messages");
    }

    @Test
    public void shouldBeAbleToConvert() {
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue("pt_br"));
        }});
        assertThat(converter.convert("10/06/2008", Calendar.class, errors, bundle), is(equalTo((Calendar) new GregorianCalendar(2008,5,10))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheDefaultLocale() throws ParseException {
        mockery.checking(new Expectations() {{
            one(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue(null));
            one(request).getSession(); will(returnValue(session));
            one(session).getAttribute("javax.servlet.jsp.jstl.fmt.locale.session"); will(returnValue(null));
            one(context).getAttribute("javax.servlet.jsp.jstl.fmt.locale.application"); will(returnValue(null));
            one(context).getInitParameter("javax.servlet.jsp.jstl.fmt.locale"); will(returnValue(null));
            one(request).getLocale(); will(returnValue(Locale.getDefault()));
        }});
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse("10/05/2010");
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        String formattedToday = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        assertThat(converter.convert(formattedToday, Calendar.class, errors, bundle), is(equalTo(cal)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToConvertEmpty() {
        assertThat(converter.convert("", Calendar.class, errors, bundle), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToConvertNull() {
        assertThat(converter.convert(null, Calendar.class, errors, bundle), is(nullValue()));
        mockery.assertIsSatisfied();
    }


    @Test
    public void shouldThrowExceptionWhenUnableToParse() {
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue("pt_br"));
        }});
        converter.convert("a,10/06/2008/a/b/c", Calendar.class, errors, bundle);
        assertThat(errors.get(0), is(VRaptorMatchers.error("", "a,10/06/2008/a/b/c is not a valid date.")));
    }

}
