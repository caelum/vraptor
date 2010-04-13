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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class RouteBuilderTest {

	private ParameterNameProvider provider;
	private RouteBuilder builder;
	private ResourceMethod method;
	private Proxifier proxifier;
	private TypeFinder typeFinder;

	public static class MyResource {

		public void method(String abc, Integer def, BigDecimal ghi) {

		}
	}

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);

		when(provider.parameterNamesFor(any(Method.class))).thenReturn(new String[] { "abc", "def", "ghi" });

		method = new DefaultResourceMethod(new DefaultResourceClass(MyResource.class), MyResource.class.getMethod(
				"method", String.class, Integer.class, BigDecimal.class));

		proxifier = new DefaultProxifier();

		typeFinder = new DefaultTypeFinder(provider);

	}

	@Test
	public void usePatternMatchinForPrimitiveParameters() throws Exception {
		builder = new RouteBuilder(proxifier, typeFinder, "/abc/{abc}/def/{def}/ghi/{ghi}");

		builder.is(MyResource.class, method.getMethod());
		Route route = builder.build();

		assertTrue("valid uri", route.canHandle("/abc/AnythingHere/def/123/ghi/123.45"));
		assertTrue("valid uri", route.canHandle("/abc/AnythingHere/def/-123/ghi/-1"));
		assertTrue("valid uri with integer decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/123"));
		assertFalse("invalid integer", route.canHandle("/abc/AnythingHere/def/Duh/ghi/123"));
		assertFalse("invalid decimal", route.canHandle("/abc/AnythingHere/def/123/ghi/kkk"));
	}

	@Test
	public void usePatternMatchingForRegexParameters() throws Exception {
		builder = new RouteBuilder(proxifier, typeFinder, "/abc/{abc:a+b+c+}/def/{def}/ghi/{ghi}");

		builder.is(MyResource.class, method.getMethod());
		Route route = builder.build();

		assertFalse("invalid uri", route.canHandle("/abc/itIsInvalid/def/123/ghi/123.45"));
		assertTrue("valid uri", route.canHandle("/abc/aaabbccccc/def/-123/ghi/-1"));

	}

	@Test
	public void usingRegexesWithCurlyBraces() throws Exception {
		builder = new RouteBuilder(proxifier, typeFinder, "/abc/{abc:[0-9A-Z]{5}}");

		builder.is(MyResource.class, method.getMethod());
		Route route = builder.build();

		assertFalse("invalid uri", route.canHandle("/abc/notValid"));
		assertFalse("invalid uri", route.canHandle("/abc/ABC123"));
		assertTrue("valid uri", route.canHandle("/abc/10AB3"));

	}
	@Test
	public void usingRegexesWithCurlyBracesNotOnTheEnd() throws Exception {
		builder = new RouteBuilder(proxifier, typeFinder, "/abc/{abc:[0-9A-Z]{5}}/");

		builder.is(MyResource.class, method.getMethod());
		Route route = builder.build();

		assertFalse("invalid uri", route.canHandle("/abc/notValid/"));
		assertFalse("invalid uri", route.canHandle("/abc/ABC123/"));
		assertTrue("valid uri", route.canHandle("/abc/10AB3/"));

	}

	@Test
	public void fillingUriForPrimitiveParameters() throws Exception {
		builder = new RouteBuilder(proxifier, typeFinder, "/abc/{abc}/def/{def}/ghi/{ghi}");

		Method method = MyResource.class.getDeclaredMethods()[0];
		builder.is(MyResource.class, method);
		Route route = builder.build();
		Object parameters = new AsmBasedTypeCreator(provider).instanceWithParameters(this.method, "Anything", 123,
				new BigDecimal("123.45"));
		String url = route.urlFor(MyResource.class, method, parameters);
		assertThat(url, is("/abc/Anything/def/123/ghi/123.45"));
	}
}
