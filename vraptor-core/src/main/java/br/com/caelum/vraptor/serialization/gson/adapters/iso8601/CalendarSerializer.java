package br.com.caelum.vraptor.serialization.gson.adapters.iso8601;

import java.lang.reflect.Type;
import java.util.Calendar;

import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CalendarSerializer implements JsonSerializer<Calendar> {

	public JsonElement serialize(Calendar calendar, Type typeOfSrc, JsonSerializationContext context) {

		String json = ISO8601Util.fromCalendar(calendar);
		
		return new JsonPrimitive(json);
	}
}