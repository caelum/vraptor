package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.validator.I18nMessage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;

public class MessageSerializer {

	public JsonElement serialize(I18nMessage message, Type type, JsonSerializationContext context) {
		String json = "{category:\"%s\",message:\"%s\"}";
		
		return new JsonParser().parse(String.format(json, message.getCategory(), message.getMessage()));
	}

}
