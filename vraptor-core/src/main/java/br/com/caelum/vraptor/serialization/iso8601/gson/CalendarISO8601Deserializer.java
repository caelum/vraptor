package br.com.caelum.vraptor.serialization.iso8601.gson;

import java.lang.reflect.Type;

import java.text.ParseException;
import java.util.Calendar;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component
public class CalendarISO8601Deserializer implements JsonDeserializer<Calendar> {
	
	private final ISO8601Util iso8601;
	
	public CalendarISO8601Deserializer(ISO8601Util iso8601) {
		this.iso8601 = iso8601;
	} 

	public Calendar deserialize(JsonElement json, Type typeOfT,	JsonDeserializationContext context) throws JsonParseException {

		try {
			String value = json.getAsString();

			Calendar calendar = iso8601.toCalendar(value);

			return calendar;
		} catch (ParseException e) {
			throw new ConversionError("Error to convert Calendar: "	+ e.getMessage());
		}
	}
}