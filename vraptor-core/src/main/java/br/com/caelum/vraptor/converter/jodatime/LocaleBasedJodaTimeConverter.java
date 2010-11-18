package br.com.caelum.vraptor.converter.jodatime;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.base.BaseLocal;

import br.com.caelum.vraptor.core.Localization;

class LocaleBasedJodaTimeConverter {

    private final Localization localization;

	public LocaleBasedJodaTimeConverter(Localization localization) {
		this.localization = localization;
	}

	public Date convert(String value, Class<? extends BaseLocal> type) throws ParseException {
		if (value == null || value.equals("")) {
			return null;
		}
		return getDateFormat(type).parse(value);
	}

	public Locale getLocale() {
		Locale locale = localization.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	private DateFormat getDateFormat(Class<? extends BaseLocal> type) {
		if (type.equals(LocalTime.class)) {
			return DateFormat.getTimeInstance(DateFormat.SHORT, getLocale());
		} else if (type.equals(LocalDateTime.class)) {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale());
		}
		
		return DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
	}
}