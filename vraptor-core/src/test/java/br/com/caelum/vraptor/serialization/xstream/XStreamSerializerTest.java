package br.com.caelum.vraptor.serialization.xstream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * testing the same cases as {@link XStreamXMLSerializationTest}
 * but using an arbitrary {@link XStream} implementation, not the {@link VRaptorXStream}. 
 * @author lucascs
 *
 */
public class XStreamSerializerTest extends XStreamXMLSerializationTest {

	@Override
	@Before
    public void setup() throws Exception {
		this.stream = new ByteArrayOutputStream();
		
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stream));
        
        
		final DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();
		this.serialization = new XStreamXMLSerialization(response, extractor, new NullProxyInitializer(), new XStreamBuilderImpl(
                new XStreamConverters(Collections.<Converter>emptyList(), Collections.<SingleValueConverter>emptyList()),
                extractor) {
			@Override
			public XStream xmlInstance() {
				return configure(new XStream() {
					{setMode(NO_REFERENCES);}
					@Override
					protected MapperWrapper wrapMapper(MapperWrapper next) {
						
					    return new MapperWrapper(next) {
					    	@Override
					    	public String serializedClass(Class type) {
					    		String superName = super.serializedClass(type);
					    		if (type.getName().equals(superName)) {
					    			return extractor.nameFor(type);
					    		}
					    		return superName;
					    	}
						};
					}
				});
			}
		});
    }

}

