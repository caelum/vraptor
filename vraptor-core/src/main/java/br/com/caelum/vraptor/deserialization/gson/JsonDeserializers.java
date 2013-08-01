package br.com.caelum.vraptor.deserialization.gson;

import java.util.List;

import com.google.gson.JsonDeserializer;

public interface JsonDeserializers {
	
	@SuppressWarnings("rawtypes")
	List<JsonDeserializer> getDeserializers();
}
