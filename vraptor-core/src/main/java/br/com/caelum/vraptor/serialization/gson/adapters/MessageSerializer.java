package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.Message;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
@RequestScoped
public class MessageSerializer implements JsonSerializer<Message> {

	public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
		String json = "{category:\"%s\",message:\"%s\"}";
		
		return new JsonParser().parse(String.format(json, message.getCategory(), message.getMessage()));
	}

}
