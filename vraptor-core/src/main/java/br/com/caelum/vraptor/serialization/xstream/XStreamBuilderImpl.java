package br.com.caelum.vraptor.serialization.xstream;

import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Implementation of default XStream configuration
 *
 * @author Rafael Viana
 * @since 3.4.0
 */
@PrototypeScoped
@Component
public class XStreamBuilderImpl implements XStreamBuilder {

	private final XStreamConverters converters;
	private final TypeNameExtractor extractor;
	
	private boolean indented;
	private boolean withoutRoot;
	
	public XStreamBuilderImpl(XStreamConverters converters, TypeNameExtractor extractor) {
		this.converters = converters;
		this.extractor = extractor;
	}

	public static XStreamBuilder cleanInstance(Converter...converters) {
        return new XStreamBuilderImpl(
                new XStreamConverters(Arrays.asList(converters), Collections.<SingleValueConverter>emptyList()),
                new DefaultTypeNameExtractor());
    }

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
    
	public XStream jsonInstance() {
		XStream xstream = new XStream(getHierarchicalStreamDriver()) {
            {setMode(NO_REFERENCES);}
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new VRaptorClassMapper(next, extractor);
            }
        };
		converters.registerComponents(xstream);
		return xstream;
	}

    /**
      * You can override this method for configuring Driver before serialization
      * @return configured hierarchical driver
      */
    protected HierarchicalStreamDriver getHierarchicalStreamDriver() {
        final String newLine = (indented ? INDENTED_NEW_LINE : DEFAULT_NEW_LINE);
        final char[] lineIndenter = (indented ? INDENTED_LINE_INDENTER : DEFAULT_LINE_INDENTER);

        return new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer writer) {
                if (withoutRoot) {
                    return new JsonWriter(writer, lineIndenter, newLine, JsonWriter.DROP_ROOT_MODE);
                }

                return new JsonWriter(writer, lineIndenter, newLine);
            }
        };
    }

	public XStreamBuilder indented() {
		indented = true;
        return this;
	}

	public XStreamBuilder withoutRoot() {
		withoutRoot = true;
        return this;
	}
	
}