package br.com.caelum.vraptor.converter.jodatime;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import org.joda.time.LocalDate;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;

@Convert(LocalDate.class)
public class LocalDateConverter implements Converter<LocalDate> {

	private final LocaleBasedCalendarConverter delegate;

	public LocalDateConverter(LocaleBasedCalendarConverter delegate) {
		this.delegate = delegate;
	}

	public LocalDate convert(String value, Class<? extends LocalDate> type, ResourceBundle bundle) {
		Calendar calendar = delegate.convert(value, Calendar.class, bundle);
		if (calendar == null) {
			return null;
		}
		try {
            return LocalDate.fromCalendarFields(calendar);
        } catch (Exception e) {
        	throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_date"), value));
        }
	}
}
