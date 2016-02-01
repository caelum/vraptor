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
package br.com.caelum.vraptor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.ioc.Component;

/**
 * Helper class for handling ISO8601 strings of the following format:
 * "1982-06-10T05:00:00.000-03:00". It also supports parsing the "Z" timezone.
 * 
 * @author Rafael Dipold
 */
@Component
public final class ISO8601Util {

	/** Default Extended Format */
	private static final String DEFAULT_ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private static final String REGEX_ISO8601 = "^(\\d{4})-?(\\d\\d)-?(\\d\\d)(?:T(\\d\\d)(?::?(\\d\\d)(?::?(\\d\\d)(?:\\.(\\d+))?)?)?(Z|([+-])(\\d\\d):?(\\d\\d)?)?)?$";
	//						1	 2	 3	   4		5		6		 7	   8  9	 10	11

	private final SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_ISO8601_FORMAT); 
	
	/** Transform Calendar to ISO8601 string. */
	public String fromCalendar(final Calendar calendar) {
		formatter.setTimeZone(calendar.getTimeZone());
		return fromDate(calendar.getTime());
	}

	/** Transform java.util.Date to ISO8601 string. */
	public String fromDate(final Date date) {
		String formatted = formatter.format(date);
		formatted = formatted.replaceAll("[+-]00:?00$", "Z");
		return formatted;
	}

	/** Get current date and time formatted as ISO8601 string. */
	public String now() {
		return fromCalendar(GregorianCalendar.getInstance());
	}

	/** Transform ISO8601 string to Calendar 
	 * @throws ParseException */
	public Calendar toCalendar(final String iso8601String) throws ParseException {
		Pattern pattern = Pattern.compile(REGEX_ISO8601);
		Matcher matcher = pattern.matcher(iso8601String);
		
		if (matcher.matches()) {
			int year	= matcher.group(1) != null ? Integer.valueOf(matcher.group(1)) : 0;
			int month	= matcher.group(2) != null ? Integer.valueOf(matcher.group(2)) - 1 : 0;
			int day		= matcher.group(3) != null ? Integer.valueOf(matcher.group(3)) : 0;
			
			int h	= matcher.group(4) != null ? Integer.valueOf(matcher.group(4)) : 0;
			int m	= matcher.group(5) != null ? Integer.valueOf(matcher.group(5)) : 0;
			int s	= matcher.group(6) != null ? Integer.valueOf(matcher.group(6)) : 0;
			int ms	= Math.round(Float.parseFloat("0." + (matcher.group(7) != null ? matcher.group(7) : "0")) * 1000);

			TimeZone timeZone = TimeZone.getTimeZone("GMT" + (matcher.group(8) != null ? matcher.group(8) : ""));
			
			Calendar calendar = GregorianCalendar.getInstance(timeZone);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, h);
			calendar.set(Calendar.MINUTE, m);
			calendar.set(Calendar.SECOND, s);
			calendar.set(Calendar.MILLISECOND, ms);
			
			return calendar;
		} 
		else
			throw new java.text.ParseException("Unparseable ISO8601 date format: " + iso8601String, 0);
	}

	/** Transform ISO8601 string to java.util.Date */
	public Date toDate(final String iso8601String) throws ParseException {
		Calendar calendar = toCalendar(iso8601String);
		return calendar.getTime();
	}
}
