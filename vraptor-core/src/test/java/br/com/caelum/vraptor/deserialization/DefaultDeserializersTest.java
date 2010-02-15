package br.com.caelum.vraptor.deserialization;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ioc.Container;

public class DefaultDeserializersTest {


	private Deserializers deserializers;
	@Mock private Container container;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		deserializers = new DefaultDeserializers();
	}

	@Test
	public void shouldThrowExceptionWhenThereIsNoDeserializerRegisteredForGivenContentType() throws Exception {
		assertNull(deserializers.deserializerFor("bogus content type", container));
	}

	static interface NotAnnotatedDeserializer extends Deserializer {}

	@Test(expected=IllegalArgumentException.class)
	public void allDeserializersMustBeAnnotatedWithDeserializes() throws Exception {
		deserializers.register(NotAnnotatedDeserializer.class);
	}

	@Deserializes({"application/xml", "json"})
	static interface MyDeserializer extends Deserializer {}

	@Test
	public void shouldNotCallDeserializerIfItDoesntAcceptGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		assertNull(deserializers.deserializerFor("image/jpeg", container));

		verify(container, never()).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/xml", container);

		verify(container).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentTypeIfVendorized() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/json", container);

		verify(container).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentTypeIfAtomizedAlike() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/atom+json", container);

		verify(container).instanceFor(MyDeserializer.class);
	}
}
