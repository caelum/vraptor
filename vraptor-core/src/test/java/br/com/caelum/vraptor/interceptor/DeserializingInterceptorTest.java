package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.view.Status;


public class DeserializingInterceptorTest {
	private DeserializingInterceptor interceptor;
	private DefaultResourceMethod consumeXml;
	private DefaultResourceMethod doesntConsume;
	@Mock private HttpServletRequest request;
	@Mock private InterceptorStack stack;
	@Mock Deserializers deserializers;
	private MethodInfo methodInfo;
	@Mock Container container;
	@Mock private Status status;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		methodInfo = new DefaultMethodInfo();
		interceptor = new DeserializingInterceptor(request, deserializers, methodInfo, container, status);
		consumeXml = new DefaultResourceMethod(null, DummyResource.class.getDeclaredMethod("consumeXml"));
		doesntConsume = new DefaultResourceMethod(null, DummyResource.class.getDeclaredMethod("doesntConsume"));
	}


	static class DummyResource {
		@Consumes("application/xml")
		public void consumeXml() {}

		@Consumes()
		public void consumesAnything() {}

		public void doesntConsume() {}
	}
	@Test
	public void shouldOnlyAcceptMethodsWithConsumesAnnotation() throws Exception {
		assertTrue(interceptor.accepts(consumeXml));
		assertFalse(interceptor.accepts(doesntConsume));
	}

	@Test
	public void willSetHttpStatusCode415IfTheResourceMethodDoesNotSupportTheGivenMediaTypes() throws Exception {
		when(request.getContentType()).thenReturn("image/jpeg");

		interceptor.intercept(stack, consumeXml, null);
		verify(status).unsupportedMediaType("Request with media type [image/jpeg]. Expecting one of [application/xml].");
		verifyZeroInteractions(stack);
	}


	@Test
	public void willSetHttpStatusCode415IfThereIsNoDeserializerButIsAccepted() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(null);

		interceptor.intercept(stack, consumeXml, null);
		verify(status).unsupportedMediaType("Unable to handle media type [application/xml]: no deserializer found.");
		verifyZeroInteractions(stack);
	}

	@Test
	public void willSetMethodParametersWithDeserializationAndContinueStackAfterDeserialization() {
		when(request.getContentType()).thenReturn("application/xml");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[2]);
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		interceptor.intercept(stack, consumeXml, null);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
		verify(stack).next(consumeXml, null);
	}

	@Test
	public void willSetMethodParametersWithDeserializationEvenIfTheContentTypeHasCharsetDeclaration() {
		when(request.getContentType()).thenReturn("application/xml; charset=UTF-8");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[2]);
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		interceptor.intercept(stack, consumeXml, null);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
		verify(stack).next(consumeXml, null);
	}

	@Test
	public void willDeserializeForAnyContentTypeIfPossible() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");

		methodInfo.setParameters(new Object[2]);
		final DefaultResourceMethod consumesAnything = new DefaultResourceMethod(null, DummyResource.class.getDeclaredMethod("consumesAnything"));

		final Deserializer deserializer = mock(Deserializer.class);
		when(deserializer.deserialize(null, consumesAnything)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		interceptor.intercept(stack, consumesAnything, null);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
		verify(stack).next(consumesAnything, null);
	}

	@Test
	public void willSetOnlyNonNullParameters() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[] {"original1", "original2"});
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {null, "deserialized"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		interceptor.intercept(stack, consumeXml, null);

		assertEquals(methodInfo.getParameters()[0], "original1");
		assertEquals(methodInfo.getParameters()[1], "deserialized");
		verify(stack).next(consumeXml, null);
	}

}
