package br.com.caelum.vraptor.deserialization.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.ResultException;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
@SuppressWarnings("rawtypes")
public class GsonDeserialization implements Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(GsonDeserialization.class);

	private final ParameterNameProvider paramNameProvider;

	private final Collection<JsonDeserializer> adapters;

	private final HttpServletRequest request;

	public GsonDeserialization(ParameterNameProvider paramNameProvider, JsonDeserializers adapters, HttpServletRequest request) {
		this.paramNameProvider = paramNameProvider;
		this.adapters = adapters.getDeserializers();
		this.request = request;
	}

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		Class<?>[] types = getTypes(method);

		if (types.length == 0) {
			throw new IllegalArgumentException(
					"Methods that consumes representations must receive at least one argument");
		}

		Gson gson = getGson();

		Object[] params = new Object[types.length];
		String[] parameterNames = paramNameProvider.parameterNamesFor(method.getMethod());

		try {
			String content = getContentOfStream(inputStream);

			if (Strings.isNullOrEmpty(content)) {
				logger.debug("json with no content");
				return params;
			}

			logger.debug("json retrieved: {}", content);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(content);

			if (jsonElement.isJsonObject()) {
				JsonObject root = jsonElement.getAsJsonObject();
				
				for (int i = 0; i < types.length; i++) {
					String name = parameterNames[i];
					JsonElement node = root.get(name);
					
					if (isWithoutRoot(parameterNames, root)) {
						params[i] = gson.fromJson(root, types[i]);
						logger.info("json without root deserialized");
						break;
					} else if (node != null) {
						if (node.isJsonArray()) {
							JsonArray jsonArray = node.getAsJsonArray();
							
							Type type = method.getMethod().getGenericParameterTypes()[i];
							if (type instanceof ParameterizedType) {
								params[i] = gson.fromJson(jsonArray, type);
							} else {
								params[i] = gson.fromJson(jsonArray, types[i]);
							}
						} else {
							params[i] = gson.fromJson(node, types[i]);
						}
					}
				}
			} else if (jsonElement.isJsonArray()) {
				if ((parameterNames.length != 1) || (!(method.getMethod().getGenericParameterTypes()[0] instanceof ParameterizedType))) {
			throw new IllegalArgumentException("Methods that consumes an array representation must receive only just one collection generic typed argument");
				}
				
				JsonArray jsonArray= jsonElement.getAsJsonArray();
				params[0] = gson.fromJson(jsonArray, method.getMethod().getGenericParameterTypes()[0]);
			}
		} catch (Exception e) {
			throw new ResultException("Unable to deserialize data", e);
		}

		logger.debug("json deserialized: {}", (Object) params);
		return params;
	}

	private boolean isWithoutRoot(String[] parameterNames, JsonObject root) {
		for(String parameterName : parameterNames) {
			if(root.get(parameterName) != null) return false;
		}
		return true;
	}

	protected Class<?>[] getTypes(ResourceMethod method) {
		Class<?> genericType = getSuperClassTypeArgument(method);

		if (genericType != null) {
			return parseGenericParameters(method.getMethod(), genericType);
		}

		return method.getMethod().getParameterTypes();
	}

	private static Class<?>[] parseGenericParameters(Method method, Class<?> genericType) {
		Class<?>[] paramClasses = method.getParameterTypes();
		Type[] paramTypes = method.getGenericParameterTypes();

		Class<?>[] result = new Class<?>[paramTypes.length];

		for (int i = 0; i < paramTypes.length; i++) {
			Type paramType = paramTypes[i];

			if (paramType instanceof TypeVariable) {
				result[i] = genericType;
			} else {
				result[i] = paramClasses[i];
			}
		}

		return result;
	}

	private static Class<?> getSuperClassTypeArgument(ResourceMethod method) {
		Type genericType = method.getResource().getType().getGenericSuperclass();

		if (genericType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
		}

		return null;
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
		String charset = getRequestCharset();
		logger.debug("Using charset {}", charset);
		return CharStreams.toString(new InputStreamReader(input, charset));
	}

	private String getRequestCharset() {
		String charset = Objects.firstNonNull(request.getHeader("Accept-Charset"), "UTF-8");
		return charset.split(",")[0];
	}
}
