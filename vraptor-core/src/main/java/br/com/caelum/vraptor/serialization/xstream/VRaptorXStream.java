package br.com.caelum.vraptor.serialization.xstream;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.common.base.Supplier;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public final class VRaptorXStream extends XStream {
	private final TypeNameExtractor extractor;

	{setMode(NO_REFERENCES);}

	public VRaptorXStream(TypeNameExtractor extractor) {
		super();
		this.extractor = extractor;
	}
	public VRaptorXStream(TypeNameExtractor extractor, HierarchicalStreamDriver hierarchicalStreamDriver) {
		super(hierarchicalStreamDriver);
		this.extractor = extractor;
	}

	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
	    return new VRaptorClassMapper(next, new Supplier<TypeNameExtractor>() {
			public TypeNameExtractor get() {
				return extractor;
			}
		});
	}
}