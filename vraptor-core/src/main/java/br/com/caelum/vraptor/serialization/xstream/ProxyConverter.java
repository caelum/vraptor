package br.com.caelum.vraptor.serialization.xstream;

import br.com.caelum.vraptor.serialization.ProxyInitializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public final class ProxyConverter implements Converter {
	private final ProxyInitializer initializer;
	private final XStream xstream;

	public ProxyConverter(ProxyInitializer initializer, XStream xstream) {
		this.initializer = initializer;
		this.xstream = xstream;
	}
	public boolean canConvert(Class clazz) {
		return initializer.isProxy(clazz);
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new AssertionError();
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Converter converter = xstream.getConverterLookup().lookupConverterForType(initializer.getActualClass(value));
		initializer.initialize(value);
		converter.marshal(value, writer, context);
	}
}