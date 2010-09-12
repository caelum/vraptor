package br.com.caelum.vraptor.restfulie.serialization;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class RestfulSerializationTest {

	class CustomType implements HypermediaResource{

		public void configureRelations(RelationBuilder builder) {
		}

	}

	@Test
	public void shouldReturnAnXStreamInstanceWithSupportToLinkConvertersBasedOnReflection() {
		RestfulSerialization serialization = new RestfulSerialization(null, null, null, null, null);
		XStream xstream = serialization.getXStream();
		Converter converter = xstream.getConverterLookup().lookupConverterForType(CustomType.class);
		assertThat(converter.getClass(), is(typeCompatibleWith(LinkConverter.class)));
	}

	class CustomNonHMType {

	}

	@Test
	public void shouldUseTheDefaultConverterForTypesThatAreNotHypermediaAware() {
		RestfulSerialization serialization = new RestfulSerialization(null, null, null, null, null);
		XStream xstream = serialization.getXStream();
		Converter converter = xstream.getConverterLookup().lookupConverterForType(CustomNonHMType.class);
		assertThat(converter.getClass(), is(typeCompatibleWith(ReflectionConverter.class)));
	}

	class MegaConverter implements Converter {

		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			return null;
		}

		@SuppressWarnings("unchecked")
		public boolean canConvert(Class type) {
			return true;
		}

	}

	@Test
	public void shouldAllowCustomXStreamRetrieval() {

		RestfulSerialization serialization = new RestfulSerialization(null, null, null, null, null) {
			@Override
			protected XStream getXStream() {
				XStream xStream = super.getXStream();
				xStream.registerConverter(new MegaConverter());
				return xStream;
			}
		};
		XStream xstream = serialization.getXStream();
		Converter converter = xstream.getConverterLookup().lookupConverterForType(CustomType.class);
		assertThat(converter.getClass(), is(typeCompatibleWith(MegaConverter.class)));
		converter = xstream.getConverterLookup().lookupConverterForType(CustomNonHMType.class);
		assertThat(converter.getClass(), is(typeCompatibleWith(MegaConverter.class)));
	}

}
