package br.com.caelum.vraptor.deserialization;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class DeserializesHandlerTest {

	private DeserializesHandler handler;
	private Deserializers deserializers;

	@Before
	public void setUp() throws Exception {
		deserializers = mock(Deserializers.class);
		handler = new DeserializesHandler(deserializers);
	}

	@Test
	public void shouldAcceptDeserializesAnnotation() throws Exception {
		assertEquals(handler.stereotype(), Deserializes.class);
	}

	static interface MyDeserializer extends Deserializer{}
	static interface NotADeserializer{}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionWhenTypeIsNotADeserializer() throws Exception {
		handler.handle(NotADeserializer.class);
	}

	@Test
	public void shouldRegisterTypesOnDeserializers() throws Exception {
		handler.handle(MyDeserializer.class);

		verify(deserializers).register(MyDeserializer.class);
	}
}
