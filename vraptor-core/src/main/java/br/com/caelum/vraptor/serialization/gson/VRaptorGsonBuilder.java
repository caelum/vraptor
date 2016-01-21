package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.xstream.Serializee;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

/**
 *
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@SuppressWarnings("rawtypes")
@Component
public class VRaptorGsonBuilder {

	protected GsonBuilder builder = new GsonBuilder();

	private boolean withoutRoot;

	private String alias;

	private final Collection<JsonSerializer> serializers;

	private final Collection<ExclusionStrategy> exclusions;

	public VRaptorGsonBuilder(JsonSerializers serializers, Serializee serializee) {
		this.serializers = serializers.getSerializers();
		ExclusionStrategy exclusion = new Exclusions(serializee);
		this.exclusions = Arrays.asList(exclusion);

	}

	public boolean isWithoutRoot() {
		return withoutRoot;
	}

	public void setWithoutRoot(boolean withoutRoot) {
		this.withoutRoot = withoutRoot;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void indented() {
		builder.setPrettyPrinting();
	}

	public void setExclusionStrategies(ExclusionStrategy... strategies) {
		builder.setExclusionStrategies(strategies);
	}

	public Gson create() {
		for (JsonSerializer<?> adapter : serializers) {
			builder.registerTypeHierarchyAdapter(getAdapterType(adapter), adapter);
		}

		for (ExclusionStrategy exclusion : exclusions) {
			builder.addSerializationExclusionStrategy(exclusion);
		}

		return builder.create();
	}

	private Class<?> getAdapterType(JsonSerializer<?> adapter) {
		Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
		ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		Type actualType = type.getActualTypeArguments()[0];

		return (Class<?>) actualType;
	}
}