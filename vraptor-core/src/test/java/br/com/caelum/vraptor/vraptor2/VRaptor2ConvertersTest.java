package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.LogicRequest;
import org.vraptor.converter.ConversionException;
import org.vraptor.converter.Converter;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class VRaptor2ConvertersTest {

    private Mockery mockery;
    private Config config;
    private Container container;
    
    public static class VRaptor2BasedConverter implements Converter{

        public Object convert(String value, Class<?> type, LogicRequest context) throws ConversionException {
            return null;
        }

        public Class<?>[] getSupportedTypes() {
            return new Class<?>[]{Integer.class};
        }
        
    }

    @Convert(Integer.class)
    public static class VRaptor3BasedConverter implements br.com.caelum.vraptor.Converter<Integer>{

        public Integer convert(String value, Class<? extends Integer> type) {
            return null;
        }
        
    }

    @Before
    public void setup() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.mockery = new Mockery();
        this.config = mockery.mock(Config.class);
        this.container = mockery.mock(Container.class);
    }

    @Test
    public void usesVRaptor2ConverterIfRegistered() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        mockery.checking(new Expectations() {
            {
                one(config).getConverters(); will(returnValue(Arrays.asList(new String[]{VRaptor2BasedConverter.class.getName()})));
            }
        });
        VRaptor2Converters converters = new VRaptor2Converters(config);
        br.com.caelum.vraptor.Converter<?> converter = converters.to(Integer.class, container);
        assertThat(converter.getClass(), is(typeCompatibleWith(ConverterWrapper.class)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void usesVRaptor3ConverterIfRegistered() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Converters delegate = mockery.mock(Converters.class);
        mockery.checking(new Expectations() {
            {
                one(config).getConverters(); will(returnValue(Arrays.asList(new String[0])));
                one(delegate).to(Integer.class, container); will(returnValue(new VRaptor3BasedConverter()));
            }
        });
        final VRaptor2Converters converters = new VRaptor2Converters(config, delegate);
        assertThat(converters.to(Integer.class, container).getClass(), is(typeCompatibleWith(VRaptor3BasedConverter.class)));
        mockery.assertIsSatisfied();
    }

}
