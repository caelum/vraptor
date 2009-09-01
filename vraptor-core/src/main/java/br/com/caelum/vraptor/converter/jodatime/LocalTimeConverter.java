package br.com.caelum.vraptor.converter.jodatime;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import org.joda.time.LocalTime;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;

@Convert(LocalTime.class)
public class LocalTimeConverter implements Converter<LocalTime> {

	private final LocaleBasedCalendarConverter delegate;

	public LocalTimeConverter(LocaleBasedCalendarConverter delegate) {
		this.delegate = delegate;
	}

	public LocalTime convert(String value, Class<? extends LocalTime> type, ResourceBundle bundle) {
		Calendar calendar = delegate.convert(value, Calendar.class, bundle);
		if (calendar == null) {
			return null;
		}
		try {
            return LocalTime.fromCalendarFields(calendar);
        } catch (Exception e) {
        	throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_date"), value));
        }
	}
}
