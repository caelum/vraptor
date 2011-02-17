package br.com.caelum.vraptor.deserialization;

import java.io.Writer;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.serialization.xstream.VRaptorClassMapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Implementation of default XStream configuration
 *
 * @author Rafael Viana
 */
@PrototypeScoped
@Component
public class XStreamBuilderImpl implements XStreamBuilder {

	private final XStreamConverters converters;
	private final TypeNameExtractor extractor;
	
	private HierarchicalStreamDriver driver;
	private boolean indented;
	private boolean withoutRoot;
	
	public XStreamBuilderImpl(XStreamConverters converters, TypeNameExtractor extractor)
	{
		this.converters = converters;
		this.extractor = extractor;
	}

	@Override
	public XStream xmlInstance() {
		XStream xstream = new XStream() {
			{setMode(NO_REFERENCES);}
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new VRaptorClassMapper(next, extractor);
			}
		};
		converters.registerComponents(xstream);
		return xstream;
	}
	
	protected static final String DEFAULT_NEW_LINE = "";
    protected static final char[] DEFAULT_LINE_INDENTER = {};
    
    protected static final String INDENTED_NEW_LINE = "\n";
    protected static final char[] INDENTED_LINE_INDENTER = { ' ', ' '};
    
	@Override
	public XStream jsonInstance() {
		
		final String newLine = (indented ? INDENTED_NEW_LINE : DEFAULT_NEW_LINE);
        final char[] lineIndenter = (indented ? INDENTED_LINE_INDENTER : DEFAULT_LINE_INDENTER);
        
		driver = new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer writer) {
                if (withoutRoot) {
                    return new JsonWriter(writer, lineIndenter, newLine, JsonWriter.DROP_ROOT_MODE);
                }

                return new JsonWriter(writer, lineIndenter, newLine);
            }
        };
        
		XStream xstream = new XStream(driver) {
            {setMode(NO_REFERENCES);}
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new VRaptorClassMapper(next, extractor);
            }
        };
		converters.registerComponents(xstream);
		return xstream;
	}

	@Override
	public XStreamBuilder indented() {
		indented = true;
        return this;
	}

	@Override
	public XStreamBuilder withoutRoot() {
		withoutRoot = true;
        return this;
	}
	
}