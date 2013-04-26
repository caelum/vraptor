package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component
public class CalendarDeserializer implements JsonDeserializer<Calendar> {

	private final Localization localization;

	public CalendarDeserializer(Localization localization) {
		this.localization = localization;
	}

	public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		String value = json.getAsString();

		Locale locale = localization.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}

		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		try {
			Date date = format.parse(value);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return calendar;
		} catch (ParseException e) {
			throw new ConversionError("Error to convert Calendar.");
		}

	}

}
