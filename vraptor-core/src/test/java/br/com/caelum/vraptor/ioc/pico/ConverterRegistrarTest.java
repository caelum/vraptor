package br.com.caelum.vraptor.ioc.pico;

import static java.util.Arrays.asList;

import java.util.ResourceBundle;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;

/**
 * @author Fabio Kung
 */
public class ConverterRegistrarTest {

    private Mockery mockery;
    private Registrar registrar;
    private ComponentRegistry componentRegistry;
    private Converters converters;
    private Scanner scanner;

    @Before
    public void setUp() {
        this.mockery = new Mockery();
        this.componentRegistry = mockery.mock(ComponentRegistry.class);
        this.converters = mockery.mock(Converters.class);
        this.scanner = mockery.mock(Scanner.class);
        this.registrar = new ConverterRegistrar(converters);
    }

    @Test(expected = VRaptorException.class)
    public void shouldNotAcceptAConverterThatDoesNotImplementTheCorrectInterface() throws Exception {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Convert.class);
                will(returnValue(asList(ConverterNotOk.class)));
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptAConverterThatIsAnnotatedAndImplementsCorrectInterface() throws Exception {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Convert.class);
                will(returnValue(asList(ConverterOk.class)));
                one(converters).register(ConverterOk.class);
            }
        });
        this.registrar.registerFrom(scanner);
        this.mockery.assertIsSatisfied();
    }

    @Convert(String.class)
    class ConverterNotOk {

    }

    @Convert(String.class)
    class ConverterOk implements Converter<String> {

        public String convert(String value, Class<? extends String> type, ResourceBundle bundle) {
            return null;
        }

    }

    class ConverterNotAnnotated implements Converter<String> {

        public String convert(String value, Class<? extends String> type, ResourceBundle bundle) {
            return null;
        }
    }

}
