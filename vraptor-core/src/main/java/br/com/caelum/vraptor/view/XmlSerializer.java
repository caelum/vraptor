package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlSerializer {
	
	private Writer writer;
	private Object analyzing;
	private final List<String> excludes = new ArrayList<String>();
	private final Map<String, XmlSerializer> includes = new HashMap<String, XmlSerializer>();
	private final XmlSerializer parent;

	public XmlSerializer(OutputStream output) {
		this(new OutputStreamWriter(output));
	}
	
	public XmlSerializer(XmlSerializer parent, Writer writer) {
		this.parent = parent;
		this.writer = writer;
	}

	public XmlSerializer(Writer writer) {
		this(null, writer);
	}

	public <T> XmlSerializer from(T object) {
		this.analyzing = object;
		return this;
	}
	private void parseFields(Object object, Class type) throws IOException {
		if(type.equals(Object.class)) {
			return;
		}
		Field[] fields = type.getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				XmlSerializer serializer = includes.get(field.getName());
				if(serializer!=null) {
					serializer.from(field.get(object)).serializeForReal();
					continue;
				}
				boolean shouldExclude = excludes.contains(field.getName());
				if(!shouldExclude && (field.getType().isPrimitive() || field.getType().equals(String.class))) {
						Object result = field.get(object);
						writer.write("  " + startTag(field.getName()) + result + endTag(field.getName()) + "\n");
				}
			}
		} catch (IllegalArgumentException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		} catch (IllegalAccessException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		}
		parseFields(object, type.getSuperclass());
	}

	private String simpleNameFor(String name) {
		if(name.length()==1) {
			return name.toLowerCase();
		}
		StringBuilder content = new StringBuilder();
		content.append(Character.toLowerCase(name.charAt(0)));
		for(int i=1;i<name.length();i++) {
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				content.append("_");
				content.append(Character.toLowerCase(c));
			} else {
				content.append(c);
			}
		}
		return content.toString();
	}

	private String endTag(String name) {
		return "</" + name + ">";
	}

	private String startTag(String name) {
		return "<" + name + ">";
	}

	public XmlSerializer exclude(String... names) {
		for(String fieldName : names) {
			this.excludes .add(fieldName);
		}
		return this;
	}

	public void serialize() {
		if(this.parent!=null) {
			parent.serialize();
			return;
		}
		serializeForReal();
	}

	private void serializeForReal() {
		String name = simpleNameFor(analyzing.getClass().getSimpleName());
		try {
			writer.write("<" + name + ">\n");
			parseFields(analyzing, analyzing.getClass());
			writer.write("</" + name + ">");
			writer.flush();
		} catch (SecurityException e) {
			throw new SerializationException("Unable to serialize " + analyzing, e);
		} catch (IOException e) {
			throw new SerializationException("Unable to serialize " + analyzing, e);
		}
	}

	public XmlSerializer include(String fieldName) {
		XmlSerializer serializer = new XmlSerializer(this, writer);
		this.includes.put(fieldName, serializer);
		return serializer;
	}

}
