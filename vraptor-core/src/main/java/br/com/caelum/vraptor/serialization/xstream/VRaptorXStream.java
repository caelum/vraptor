package br.com.caelum.vraptor.serialization.xstream;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.common.base.Supplier;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public final class VRaptorXStream extends XStream {
	private final TypeNameExtractor extractor;
	private VRaptorClassMapper vraptorMapper;

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
		
	    vraptorMapper = new VRaptorClassMapper(next,
	    /* this method is called in the super constructor, so we cannot use instance variables, so we're
	     * using this 'lazy' get */
	    new Supplier<TypeNameExtractor>() {
			public TypeNameExtractor get() {
				return extractor;
			}
		});
		return vraptorMapper;
	}
	public VRaptorClassMapper getVRaptorMapper() {
		return vraptorMapper;
	}
}