package br.com.caelum.vraptor.serialization.xstream;

import static br.com.caelum.vraptor.serialization.xstream.VRaptorClassMapper.isPrimitive;
import static com.google.common.base.Objects.firstNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;

public class OldAndProbablyBuggyConfigurer {

	private final XStream xstream;

	public OldAndProbablyBuggyConfigurer(XStream xstream) {
		this.xstream = xstream;
	}

	public void configure(Serializee serializee) {
		Multimap<Class<?>, String> excludesMap = LinkedListMultimap.create();
		if (!serializee.isRecursive()) {
			Class<?> type = serializee.getRootClass();
			excludeNonPrimitiveFields(excludesMap, type);
			
			for (Class<?> eType : firstNonNull(serializee.getElementTypes(), Collections.<Class<?>>emptySet())) {
				excludeNonPrimitiveFields(excludesMap, eType);
			}
		}
		for (Entry<String, Class<?>> exclude : serializee.getExcludes().entries()) {
			parseExclude(exclude);
		}
		for (Entry<String, Class<?>> include : serializee.getIncludes().entries()) {
			parseInclude(excludesMap, include);
		}
		
		for (Entry<Class<?>, String> exclude : excludesMap.entries()) {
			xstream.omitField(exclude.getKey(), exclude.getValue());
		}		
	}
	
	private void parseExclude(Entry<String, Class<?>> exclude) {
		xstream.omitField(exclude.getValue(), getNameFor(exclude.getKey()));
	}


	private void parseInclude(Multimap<Class<?>, String> excludesMap, Entry<String, Class<?>> include) {
		Class<?> parentType = include.getValue();
		String fieldName = getNameFor(include.getKey());
		Field field = new Mirror().on(parentType).reflect().field(fieldName);
		if (field == null) return;
		Type genericType = field.getGenericType();
		Class<?> fieldType = Serializee.getActualType(genericType);

		if (!excludesMap.containsKey(fieldType)) {
			excludeNonPrimitiveFields(excludesMap, fieldType);
		}
		excludesMap.remove(parentType, fieldName);
	}
	
	private String getNameFor(String name) {
		String[] path = name.split("\\.");
		return path[path.length-1];
	}
	
	private void excludeNonPrimitiveFields(Multimap<Class<?>, String> excludesMap, Class<?> type) {
		for (Field field : new Mirror().on(type).reflectAll().fields()) {
			if (!isPrimitive(field.getType())) {
				excludesMap.put(field.getDeclaringClass(), field.getName());
			}
		}
	}

}
