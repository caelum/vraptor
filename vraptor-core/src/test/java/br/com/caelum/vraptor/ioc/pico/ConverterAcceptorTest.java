package br.com.caelum.vraptor.ioc.pico;

import java.util.ResourceBundle;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;

public class ConverterAcceptorTest {

	private Mockery mockery;
	private Acceptor acceptor;
	private Converters converters;

	@Before
	public void setUp() {
		this.mockery = new Mockery();
		this.converters = mockery.mock(Converters.class);
		this.acceptor = new ConverterAcceptor(converters);
	}

	@Test(expected = VRaptorException.class)
	public void shouldNotAcceptAConverterThatDoesNotImplementTheCorrectInterface() throws Exception {

		mockery.checking(new Expectations() {
			{
				never(converters).register(with(any(Class.class)));
			}
		});
		this.acceptor.analyze(ConverterNotOk.class);
		this.mockery.assertIsSatisfied();
	}

	@Test
	public void shouldNotAcceptAConverterThatIsNotAnnotated() throws Exception {
		mockery.checking(new Expectations() {
			{
				never(converters).register(ConverterNotAnnotated.class);
			}
		});
		this.acceptor.analyze(ConverterNotAnnotated.class);
		this.mockery.assertIsSatisfied();
	}

	@Test
	public void shouldAcceptAConverterThatIsAnnotatedAndImplementsCorrectInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(converters).register(ConverterOk.class);
			}
		});
		this.acceptor.analyze(ConverterOk.class);
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
