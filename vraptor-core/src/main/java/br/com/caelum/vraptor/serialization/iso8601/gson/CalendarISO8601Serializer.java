package br.com.caelum.vraptor.serialization.iso8601.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
public class CalendarISO8601Serializer implements JsonSerializer<Calendar> {
	
	private final ISO8601Util iso8601;
	
	public CalendarISO8601Serializer(ISO8601Util iso8601) {
		this.iso8601 = iso8601;
	}

	public JsonElement serialize(Calendar calendar, Type typeOfSrc, JsonSerializationContext context) {

		String json = iso8601.fromCalendar(calendar);
		
		return new JsonPrimitive(json);
	}
}