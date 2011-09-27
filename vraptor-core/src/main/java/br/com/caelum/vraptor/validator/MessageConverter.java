package br.com.caelum.vraptor.validator;

import br.com.caelum.vraptor.ioc.Component;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@Component
public class MessageConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return Message.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object val, HierarchicalStreamWriter writer, MarshallingContext context) {
		Message message = (Message) val;
		writer.startNode("message");
		writer.setValue(message.getMessage());
		writer.endNode();
		
		writer.startNode("category");
		writer.setValue(message.getCategory());
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		return null;
	}

}
