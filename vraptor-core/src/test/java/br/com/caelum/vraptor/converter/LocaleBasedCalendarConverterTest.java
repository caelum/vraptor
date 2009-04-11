package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;

public class LocaleBasedCalendarConverterTest {

    private LocaleBasedCalendarConverter converter;
    private Mockery mockery;
    private HttpServletRequest request;
    private HttpSession session;
    private ServletContext context;

    @Before
    public void setup() {
        this.mockery =new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.session = mockery.mock(HttpSession.class);
        this.context= mockery.mock(ServletContext.class);
        final VRaptorRequest webRequest = new VRaptorRequest(context, request, null);
        this.converter = new LocaleBasedCalendarConverter(webRequest);
    }

    @Test
    public void shouldBeAbleToConvert() {
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue("pt_br"));
        }});
        assertThat(converter.convert("10/06/2008", Calendar.class), is(equalTo((Calendar) new GregorianCalendar(2008,5,10))));
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
        assertThat(converter.convert(formattedToday, Calendar.class), is(equalTo(cal)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToConvertEmpty() {
        assertThat(converter.convert("", Calendar.class), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToConvertNull() {
        assertThat(converter.convert(null, Calendar.class), is(nullValue()));
        mockery.assertIsSatisfied();
    }


    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUnableToParse() {
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"); will(returnValue("pt_br"));
        }});
        converter.convert("a,10/06/2008/a/b/c", Calendar.class);
    }

}
