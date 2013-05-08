package br.com.caelum.vraptor.serialization.gson;

import java.util.List;

import com.google.gson.JsonSerializer;

public interface JsonSerializers {

	@SuppressWarnings("rawtypes")
	List<JsonSerializer> getSerializers();
}
