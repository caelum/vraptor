package br.com.caelum.vraptor.serialization;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class VRaptorClassMapper extends MapperWrapper {

	private final TypeNameExtractor extractor;

	public VRaptorClassMapper(Mapper wrapped, TypeNameExtractor extractor) {
		super(wrapped);
		this.extractor = extractor;
	}

	@Override
	public String serializedClass(Class type) {
		String superName = super.serializedClass(type);
		if (type.getName().equals(superName)) {
			return extractor.nameFor(type);
		}
		return superName;
	}

}
