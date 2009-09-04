package br.com.caelum.vraptor.http.route;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.HttpMethod;

public class RouteBuilderTest {

	private Mockery mockery;
	private ParameterNameProvider provider;
	private RouteBuilder builder;

	public static class MyResource {

		public void method(String abc, Integer def, BigDecimal ghi) {

		}
	}
	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		provider = mockery.mock(ParameterNameProvider.class);

		mockery.checking(new Expectations() {
			{
				allowing(provider).parameterNamesFor(with(any(Method.class)));
				will(returnValue(new String[] { "abc", "def", "ghi"}));
			}
		});
		builder = new RouteBuilder(new DefaultProxifier(), new DefaultTypeFinder(provider), "/abc/{abc}/def/{def}/ghi/{ghi}");
	}

	@Test
	public void usePatternMatchinForPrimitiveParameters() throws Exception {
		builder.is(MyResource.class, MyResource.class.getDeclaredMethods()[0]);
		Route route = builder.build();

		assertTrue("valid uri", route.canHandle("/abc/AnythingHere/def/123/ghi/123.45", HttpMethod.GET));
		assertTrue("valid uri with integer decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/123", HttpMethod.GET));
		assertFalse("invalid integer", route.canHandle("/abc/AnythingHere/def/Duh/ghi/123", HttpMethod.GET));
		assertFalse("invalid decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/kkk", HttpMethod.GET));
	}
}
