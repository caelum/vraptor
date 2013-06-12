package br.com.caelum.vraptor.serialization.gson.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Type;
import java.util.ResourceBundle;

import org.junit.Test;

import br.com.caelum.vraptor.validator.I18nMessage;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public class MessageSerializerTest {

	@Test
	public void shouldSerializeI18nMessage() {
		String expectedResult = "{\"category\":\"validation\",\"message\":\"you are underage\"}";
		
		I18nMessage message = new I18nMessage("validation", "underage");
		message.setBundle(ResourceBundle.getBundle("messages"));
		JsonElement jsonElement = new MessageSerializer().serialize(message, mock(Type.class), mock(JsonSerializationContext.class));
		
		assertEquals(expectedResult, jsonElement.getAsJsonObject().toString());
	}
	
}
