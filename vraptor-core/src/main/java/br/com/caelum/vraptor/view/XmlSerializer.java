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
			Field[] fields = object.getClass().getDeclaredFields();
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
			writer.write("</" + name + ">");
			writer.flush();
		} catch (SecurityException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		} catch (IOException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		}
	}

	private String simpleNameFor(String name) {
		if(name.length()==1) {
			return name.toLowerCase();
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	private String endTag(String name) {
		return "</" + name + ">";
	}

	private String startTag(String name) {
		return "<" + name + ">";
	}

}
