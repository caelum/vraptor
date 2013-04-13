package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;
import java.util.Calendar;

import br.com.caelum.vraptor.ioc.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
public class CalendarSerializer implements JsonSerializer<Calendar> {

	public JsonElement serialize(Calendar calendar, Type typeOfSrc, JsonSerializationContext context) {
		long timeInMillis = calendar.getTimeInMillis();
		String timeZoneID = calendar.getTimeZone().getID();

		String json = "{\"time\" : \"%s\", \"timezone\" : \"%s\"}";
		json = String.format(json, timeInMillis, timeZoneID);

		return new JsonParser().parse(json).getAsJsonObject();
	}

}