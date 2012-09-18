package br.com.caelum.vraptor.deserialization.gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.ResultException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Deserializes({ "application/json", "json" })
public class GsonDeserialization implements Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(GsonDeserialization.class);

	private final ParameterNameProvider paramNameProvider;

	private final Collection<JsonDeserializer<?>> adapters;

	public GsonDeserialization(ParameterNameProvider paramNameProvider, Collection<JsonDeserializer<?>> adapters) {
		this.paramNameProvider = paramNameProvider;
		this.adapters = adapters;
	}

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		Method jMethod = method.getMethod();
		Class<?>[] types = jMethod.getParameterTypes();
		if (types.length == 0) {
			throw new IllegalArgumentException(
					"Methods that consumes representations must receive just one argument");
		}

		Gson gson = getGson();

		Object[] params = new Object[types.length];
		String[] parameterNames = paramNameProvider.parameterNamesFor(jMethod);

		try {
			String content = getContentOfStream(inputStream);
			logger.debug("json retrieved: " + content);

			JsonParser parser = new JsonParser();
			JsonObject root = (JsonObject) parser.parse(content);

			for (int i = 0; i < types.length; i++) {
				String name = parameterNames[i];
				JsonElement node = root.get(name);
				if (node != null) {
					params[i] = gson.fromJson(node, types[i]);
				}
			}
		} catch (Exception e) {
			throw new ResultException("Unable to deserialize data", e);
		}

		return params;
	}

	protected Gson getGson() {
		GsonBuilder builder = new GsonBuilder();

		for (JsonDeserializer<?> adapter : adapters) {
			builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonDeserializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		return (Class<?>) actualType;
	}

	private String getContentOfStream(InputStream input) throws IOException {
		StringBuilder content = new StringBuilder();

		byte[] buffer = new byte[1024];
		int readed = 0;
		while ((readed = input.read(buffer)) != -1) {
			content.append(new String(buffer, 0, readed));
		}

		return content.toString();
	}
}
