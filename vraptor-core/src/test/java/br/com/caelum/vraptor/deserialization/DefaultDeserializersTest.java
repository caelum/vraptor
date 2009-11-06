package br.com.caelum.vraptor.deserialization;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultDeserializersTest {


	private VRaptorMockery mockery;
	private Deserializers deserializers;
	private ResourceMethod dummyMethod;
	private Container container;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();
		container = mockery.mock(Container.class);
		deserializers = new DefaultDeserializers();

		dummyMethod = mockery.methodFor(DummyResource.class, "dummyMethod");
	}


	@After
	public void tearDown() throws Exception {
		mockery.assertIsSatisfied();
	}

	static class DummyResource {
		public void dummyMethod() {}
	}
	@Test(expected=VRaptorException.class)
	public void shouldThrowExceptionWhenThereIsNoDeserializerRegisteredForGivenContentType() throws Exception {
		deserializers.deserializerFor("bogus content type", container);
	}

	static interface NotAnnotatedDeserializer extends Deserializer {}

	@Test(expected=IllegalArgumentException.class)
	public void allDeserializersMustBeAnnotatedWithDeserializes() throws Exception {
		deserializers.register(NotAnnotatedDeserializer.class);
	}

	@Deserializes("application/xml")
	static interface MyDeserializer extends Deserializer {}

	@Test(expected=VRaptorException.class)
	public void shouldNotCallDeserializerIfItDoesntAcceptGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		mockery.checking(new Expectations() {
			{
				never(container).instanceFor(MyDeserializer.class);
			}
		});

		deserializers.deserializerFor("image/jpeg", container);
	}
	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		mockery.checking(new Expectations() {
			{
				MyDeserializer deserializer = mockery.mock(MyDeserializer.class);

				one(container).instanceFor(MyDeserializer.class); will(returnValue(deserializer));

			}
		});
		deserializers.deserializerFor("application/xml", container);
	}
}
