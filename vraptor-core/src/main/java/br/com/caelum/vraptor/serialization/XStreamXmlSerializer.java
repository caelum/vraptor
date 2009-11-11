package br.com.caelum.vraptor.serialization;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;

public class XStreamXmlSerializer implements XmlSerializer {

	private final XStream xstream;
	private final Writer writer;
	private Object toSerialize;
	private final TypeNameExtractor extractor;
	private final Multimap<Class<?>, String> excludes = LinkedListMultimap.create();

	public XStreamXmlSerializer(XStream xstream, Writer writer, TypeNameExtractor extractor) {
		this.xstream = xstream;
		this.writer = writer;
		this.extractor = extractor;
	}

	private boolean isPrimitive(Class<?> type) {
		return (type.isPrimitive() || type.getName().startsWith("java") || type.isEnum()) &&
			!Collection.class.isAssignableFrom(type);
	}

	public XmlSerializer exclude(String... names) {
		for (String name : names) {
			xstream.omitField(getTypeFor(name), getNameFor(name));
		}
		return this;
	}

	private String getNameFor(String name) {
		String[] path = name.split("\\.");
		return path[path.length-1];
	}

	private Class<?> getTypeFor(String name) {
		Class<?> type = toSerialize.getClass();
		String[] path = name.split("\\.");
		for (int i = 0; i < path.length - 1; i++) {
			type = new Mirror().on(type).reflect().field(path[i]).getType();
		}
		return type;
	}

	public <T> XmlSerializer from(T object) {
		if (object == null) {
			throw new NullPointerException("You can't serialize null objects");
		}
		if (Collection.class.isInstance(object)) {
			throw new IllegalArgumentException("It's not possible to serialize colections yet. " +
					"Create a class that wraps this collections by now.");
		}
		Class<?> type = object.getClass();
		String name = extractor.nameFor(type);
		xstream.alias(name, type);
		excludeNonPrimitiveFields(type);
		this.toSerialize = object;
		return this;
	}

	private void excludeNonPrimitiveFields(Class<?> type) {
		for (Field field : new Mirror().on(type).reflectAll().fields()) {
			if (!isPrimitive(field.getType())) {
				excludes.put(type, field.getName());
			}
		}
	}

	public XmlSerializer include(String... fields) {
		for (String field : fields) {
			excludes.remove(toSerialize.getClass(), field);
		}
		return this;
	}

	public void serialize() {
		for (Entry<Class<?>, String> exclude : excludes.entries()) {
			xstream.omitField(exclude.getKey(), exclude.getValue());
		}
		xstream.toXML(toSerialize, writer);
	}


}
