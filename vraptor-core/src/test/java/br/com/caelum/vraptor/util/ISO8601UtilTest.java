package br.com.caelum.vraptor.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class ISO8601UtilTest {

	@Test
	public void shouldParseDateExtendFormat() throws ParseException {
		Calendar parseIso8601 = ISO8601Util.toCalendar("1982-06-10");
		Calendar date = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.set(1982, 5, 10, 0, 0, 0);
		date.set(Calendar.MILLISECOND, 0);
		assertThat(parseIso8601, is(date));
	}

	@Test
	public void shouldParseDateBasicFormat() throws ParseException {
		Calendar parseIso8601 = ISO8601Util.toCalendar("19820610");
		Calendar date = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.set(1982, 5, 10, 0, 0, 0);
		date.set(Calendar.MILLISECOND, 0);
		assertThat(parseIso8601, is(date));
	}

	@Test
	public void shouldParseDateTimeExtendFormat() throws ParseException {
		Calendar parseIso8601 = ISO8601Util.toCalendar("1982-06-10T05:35:10.000-03:00");
		Calendar date = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-03:00"));
		date.set(1982, 5, 10, 5, 35, 10);
		date.set(Calendar.MILLISECOND, 0);
		assertThat(parseIso8601, is(date));
	}

	@Test
	public void shouldParseDateTimeBasicFormat() throws ParseException {
		Calendar parseIso8601 = ISO8601Util.toCalendar("19820610T05-03");
		Calendar date = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-03:00"));
		date.set(1982, 5, 10, 5, 0, 0);
		date.set(Calendar.MILLISECOND, 0);
		assertThat(parseIso8601, is(date));
	}

	@Test
	public void shouldParseDateTimeExtendFormatUTC() throws ParseException {
		Calendar parseIso8601 = ISO8601Util.toCalendar("1982-06-10T05:35:10Z");
		Calendar date = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.set(1982, 5, 10, 5, 35, 10);
		date.set(Calendar.MILLISECOND, 0);
		assertThat(parseIso8601, is(date));
	}
	
	@Test(expected=ParseException.class)
	public void expectParseExceptionOnDateWithBars() throws ParseException {
		ISO8601Util.toCalendar("1982/06/10");
	}

	@Test
	public void shouldParseCalendarToIso8601AndReconverToCalendarBrasiliaZone() throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT-03:00"));
		calendar.set(1982, 5, 10, 5, 00, 0);
		calendar.set(Calendar.MILLISECOND, 123);
		
		String iso8601Format = ISO8601Util.fromCalendar(calendar);
		
		assertThat(iso8601Format, is("1982-06-10T05:00:00.123-0300"));
		
		Calendar reconvertCalendar = ISO8601Util.toCalendar(iso8601Format);
		assertThat(calendar, is(reconvertCalendar));		
	}

	@Test
	public void shouldParseCalendarToIso8601AndReconverToCalendarUTCZone() throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.set(1982, 5, 10, 5, 00, 0);
		calendar.set(Calendar.MILLISECOND, 123);
		
		String iso8601Format = ISO8601Util.fromCalendar(calendar);
		
		assertThat(iso8601Format, is("1982-06-10T05:00:00.123Z"));
		
		Calendar reconvertCalendar = ISO8601Util.toCalendar(iso8601Format);
		assertThat(calendar, is(reconvertCalendar));		
	}
}