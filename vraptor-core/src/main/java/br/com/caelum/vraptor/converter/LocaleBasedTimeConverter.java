package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.sql.Time;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.RequestScoped;

@Convert(Time.class)
@RequestScoped
public class LocaleBasedTimeConverter implements Converter<Time> {

	private final Localization localization;

	public LocaleBasedTimeConverter(Localization localization) {
		this.localization = localization;
	}

	public Time convert(String value, Class<? extends Time> type,
			ResourceBundle bundle) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		Locale locale = localization.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}

		DateFormat formatHour = DateFormat.getTimeInstance(DateFormat.SHORT,
				locale);
		try {
			if (isUncompleteTime(value)) {
				value = value + ":00";
			}
			return new Time(formatHour.parse(value).getTime());
		} catch (ParseException pe) {

			throw new ConversionError(MessageFormat.format(
					bundle.getString("is_not_a_valid_time"), value));
		}
	}

	private boolean isUncompleteTime(String value) {
		return Pattern.compile("[0-9]{2}\\:[0-9]{2}").matcher(value).find();
	}
}
