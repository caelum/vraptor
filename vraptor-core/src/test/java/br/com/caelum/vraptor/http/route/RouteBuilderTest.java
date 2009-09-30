/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package br.com.caelum.vraptor.http.route;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class RouteBuilderTest {

	private VRaptorMockery mockery;
	private ParameterNameProvider provider;
	private RouteBuilder builder;

	public static class MyResource {

		public void method(String abc, Integer def, BigDecimal ghi) {

		}
	}
	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();
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
		assertTrue("valid uri", route.canHandle("/abc/AnythingHere/def/-123/ghi/-1", HttpMethod.GET));
		assertTrue("valid uri with integer decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/123", HttpMethod.GET));
		assertFalse("invalid integer", route.canHandle("/abc/AnythingHere/def/Duh/ghi/123", HttpMethod.GET));
		assertFalse("invalid decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/kkk", HttpMethod.GET));
	}
	@Test
	public void fillingUriForPrimitiveParameters() throws Exception {
		Method method = MyResource.class.getDeclaredMethods()[0];
		builder.is(MyResource.class, method);
		Route route = builder.build();
		Object parameters = new AsmBasedTypeCreator(provider)
			.instanceWithParameters(mockery.methodFor(MyResource.class, "method", String.class, Integer.class, BigDecimal.class),
						"Anything", 123, new BigDecimal("123.45"));
		String url = route.urlFor(MyResource.class, method, parameters);
		assertThat(url, is("/abc/Anything/def/123/ghi/123.45"));
	}
}
