package br.com.caelum.vraptor.serialization.gson.adapters.iso8601;

import java.lang.reflect.Type;
import java.util.Date;

import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateSerializer implements JsonSerializer<Date> {

	private final ISO8601Util iso8601;
	
	public DateSerializer(ISO8601Util iso8601) {
		this.iso8601 = iso8601;
	}
	
	public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {

		String json = iso8601.fromDate(date);
		
		return new JsonPrimitive(json);
	}
}