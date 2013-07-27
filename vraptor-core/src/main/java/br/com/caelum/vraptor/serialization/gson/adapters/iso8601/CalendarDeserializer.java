package br.com.caelum.vraptor.serialization.gson.adapters.iso8601;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CalendarDeserializer implements JsonDeserializer<Calendar> {

	public Calendar deserialize(JsonElement json, Type typeOfT,	JsonDeserializationContext context) throws JsonParseException {

		try {
			String value = json.getAsString();

			Calendar calendar = ISO8601Util.toCalendar(value);

			return calendar;
		} catch (ParseException e) {
			throw new ConversionError("Error to convert Calendar: "	+ e.getMessage());
		}
	}
}