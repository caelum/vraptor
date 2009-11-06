package br.com.caelum.vraptor.deserialization;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorMockery;

public class DeserializesHandlerTest {


	private VRaptorMockery mockery;
	private DeserializesHandler handler;
	private Deserializers deserializers;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();
		deserializers = mockery.mock(Deserializers.class);
		handler = new DeserializesHandler(deserializers);
	}


	@After
	public void tearDown() throws Exception {
		mockery.assertIsSatisfied();
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

		mockery.checking(new Expectations() {
			{
				one(deserializers).register(MyDeserializer.class);
			}
		});
		handler.handle(MyDeserializer.class);
	}
}
