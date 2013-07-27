package br.com.caelum.vraptor.serialization.gson.adapters.iso8601;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateDeserializer implements JsonDeserializer<Date> {

	public Date deserialize(JsonElement json, Type typeOfT,	JsonDeserializationContext context) throws JsonParseException {

		try {
			String value = json.getAsString();

			Date date = ISO8601Util.toDate(value);

			return date;
		} catch (ParseException e) {
			throw new ConversionError("Error to convert Date: "	+ e.getMessage());
		}
	}
}