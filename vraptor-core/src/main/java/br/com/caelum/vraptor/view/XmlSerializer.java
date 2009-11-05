package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;

public class XmlSerializer {
	
	
	private Writer writer;

	public XmlSerializer(OutputStream output) {
		this(new OutputStreamWriter(output));
	}

	public XmlSerializer(Writer writer) {
		this.writer = writer;
	}

	public <T> void serialize(T object) {
		String name = simpleNameFor(object.getClass().getSimpleName());
		try {
			writer.write("<" + name + ">\n");
			parseFields(object, object.getClass());
			writer.write("</" + name + ">");
			writer.flush();
		} catch (SecurityException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		} catch (IOException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		}
	}
	private <T> void parseFields(T object, Class<?> type) throws IOException {
		if(type.equals(Object.class)) {
			return;
		}
		Field[] fields = type.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.getType().isPrimitive() || field.getType().equals(String.class)) {
				try {
					Object result = field.get(object);
					writer.write("  " + startTag(field.getName()) + result + endTag(field.getName()) + "\n");
				} catch (IllegalArgumentException e) {
					throw new SerializationException("Unable to serialize " + object, e);
				} catch (IllegalAccessException e) {
					throw new SerializationException("Unable to serialize " + object, e);
				}
			}
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

}
