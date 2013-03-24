package br.com.caelum.vraptor.serialization.gson;

import static br.com.caelum.vraptor.serialization.gson.GsonSerializer.isPrimitive;

import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.xstream.Serializee;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

@Component
public class Exclusions implements ExclusionStrategy {

	private Serializee serializee;

	public Exclusions(Serializee serializee) {
		this.serializee = serializee;
	}

	public boolean shouldSkipField(FieldAttributes f) {
		String fieldName = f.getName();
		Class<?> definedIn = f.getDeclaringClass();

		for (Entry<String, Class<?>> include : serializee.getIncludes().entries()) {
			if (isCompatiblePath(include, definedIn, fieldName)) {
				return false;
			}
		}
		for (Entry<String, Class<?>> exclude : serializee.getExcludes().entries()) {
			if (isCompatiblePath(exclude, definedIn, fieldName)) {
				return true;
			}
		}

		boolean skip = false;

		if (!serializee.isRecursive())
			skip = !isPrimitive(new Mirror().on(definedIn).reflect().field(fieldName).getType());

		return skip;
	}

	private boolean isCompatiblePath(Entry<String, Class<?>> path, Class<?> definedIn, String fieldName) {
		return (path.getValue().equals(definedIn) && (path.getKey().equals(fieldName) || path.getKey().endsWith(
				"." + fieldName)));
	}

	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}