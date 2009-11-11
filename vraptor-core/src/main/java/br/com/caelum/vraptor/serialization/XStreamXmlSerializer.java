package br.com.caelum.vraptor.serialization;

import java.io.Writer;
import java.util.Collection;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.thoughtworks.xstream.XStream;

public class XStreamXmlSerializer implements XmlSerializer {

	private final XStream xstream;
	private final Writer writer;
	private Object toSerialize;
	private final TypeNameExtractor extractor;

	public XStreamXmlSerializer(XStream xstream, Writer writer, TypeNameExtractor extractor) {
		this.xstream = xstream;
		this.writer = writer;
		this.extractor = extractor;
	}

	public XmlSerializer addMethod(String methodName) {
		// TODO Auto-generated method stub
		return null;
	}

	public XmlSerializer exclude(String... names) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> XmlSerializer from(T object) {
		if (object == null) {
			throw new NullPointerException("You can't serialize null objects");
		}
		String name = extractor.nameFor(object.getClass());
		xstream.alias(name, object.getClass());
		this.toSerialize = object;
		return this;
	}

	public XmlSerializer from(String prefix, Collection collection) {
		// TODO Auto-generated method stub
		return null;
	}

	public XmlSerializer include(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	public XmlSerializer namespace(String uri, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public void serialize() {
		xstream.toXML(toSerialize, writer);
	}

}
