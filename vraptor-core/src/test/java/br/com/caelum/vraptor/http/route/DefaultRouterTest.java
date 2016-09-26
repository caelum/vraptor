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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.DefaultParameterNameProvider;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Those are more likely to be acceptance than unit tests.
 * @author guilherme silveira
 */
public class DefaultRouterTest {

	private Proxifier proxifier;
	private DefaultRouter router;
	private VRaptorRequest request;
	private ResourceMethod method;
	private Converters converters;
	private ParameterNameProvider nameProvider;

	@Before
	public void setup() {
		this.request = new VRaptorRequest(mock(HttpServletRequest.class));
		this.proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
		this.method = mock(ResourceMethod.class);
		this.converters = mock(Converters.class);
		this.nameProvider = new DefaultParameterNameProvider(new DefaultTypeNameExtractor());

		router = new DefaultRouter(new NoRoutesConfiguration(), proxifier, new NoTypeFinder(), converters, nameProvider, new JavaEvaluator());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenNoRoutesMatchTheURI() throws Exception {
		Route route = mock(Route.class);
		when(route.canHandle(anyString())).thenReturn(false);

		router.add(route);
		
		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			fail("ResourceNotFoundException is expected");
		} catch (ResourceNotFoundException e) {
			
		}
	}
	
	@Test
	public void shouldThrowMethodNotAllowedExceptionWhenNoRoutesMatchTheURIWithGivenHttpMethod() throws Exception {
		Route route = mock(Route.class);
		when(route.canHandle(anyString())).thenReturn(true);
		when(route.allowedMethods()).thenReturn(EnumSet.of(HttpMethod.GET));

		router.add(route);
		
		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			fail("MethodNotAllowedException is expected");
		} catch (MethodNotAllowedException e) {
			assertThat(e.getAllowedMethods(), is((Set<HttpMethod>)EnumSet.of(HttpMethod.GET)));
		}
	}

	@Test
	public void shouldObeyPriorityOfRoutes() throws Exception {
		Route first = mock(Route.class);
		Route second = mock(Route.class);
		
		ResourceMethod method2 = second.resourceMethod(request, "second");
		
		router.add(second);
		router.add(first);

		when(first.getPriority()).thenReturn(Path.HIGH);
		when(second.getPriority()).thenReturn(Path.LOW);
		
		EnumSet<HttpMethod> get = EnumSet.of(HttpMethod.GET);
		when(first.allowedMethods()).thenReturn(get);
		when(second.allowedMethods()).thenReturn(get);
		
		when(first.canHandle(anyString())).thenReturn(false);
		when(second.canHandle(anyString())).thenReturn(true);
		
		ResourceMethod found = router.parse("anything", HttpMethod.GET, request);
		assertThat(found, is(method2));
	}

	@Test
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		Route route = mock(Route.class);
		
		when(route.canHandle("/clients/add")).thenReturn(true);
		when(route.allowedMethods()).thenReturn(EnumSet.of(HttpMethod.POST));
		when(route.resourceMethod(request, "/clients/add")).thenReturn(method);

		router.add(route);
		ResourceMethod found = router.parse("/clients/add", HttpMethod.POST, request);
		
		assertThat(found, is(equalTo(method)));
		verify(route, atLeastOnce()).getPriority();
	}


	@Test
	public void passesTheWebMethod() throws SecurityException, NoSuchMethodException {
		HttpMethod delete = HttpMethod.DELETE;
		Route route = mock(Route.class);
		
		when(route.canHandle("/clients/add")).thenReturn(true);
		when(route.allowedMethods()).thenReturn(EnumSet.of(delete));
		when(route.resourceMethod(request, "/clients/add")).thenReturn(method);

		router.add(route);
		ResourceMethod found = router.parse("/clients/add", delete, request);
		assertThat(found, is(equalTo(method)));
		verify(route, atLeastOnce()).getPriority();
	}
	
	@Test
	public void usesTheFirstRegisteredRuleMatchingThePattern() throws SecurityException, NoSuchMethodException {
		Route route = mock(Route.class);
		Route second = mock(Route.class, "second");
		
		when(route.canHandle("/clients/add")).thenReturn(true);
		when(second.canHandle("/clients/add")).thenReturn(true);

		EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

		when(route.allowedMethods()).thenReturn(all);
		when(second.allowedMethods()).thenReturn(all);

		when(route.resourceMethod(request, "/clients/add")).thenReturn(method);
		when(route.getPriority()).thenReturn(Path.HIGHEST);
		when(second.getPriority()).thenReturn(Path.LOWEST);

		router.add(route);
		router.add(second);
		
		ResourceMethod found = router.parse("/clients/add", HttpMethod.POST, request);
		assertThat(found, is(equalTo(method)));
	}
	@Test
	public void throwsExceptionIfMoreThanOneUriMatchesWithSamePriority() {
		Route route = mock(Route.class);
		Route second = mock(Route.class, "second");
		
		when(route.canHandle("/clients/add")).thenReturn(true);
		when(second.canHandle("/clients/add")).thenReturn(true);

		EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

		when(route.allowedMethods()).thenReturn(all);
		when(second.allowedMethods()).thenReturn(all);

		when(route.getPriority()).thenReturn(Path.DEFAULT);
		when(second.getPriority()).thenReturn(Path.DEFAULT);
		
		router.add(route);
		router.add(second);
		
		try {
			router.parse("/clients/add", HttpMethod.POST, request);
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("/clients/add").with(HttpMethod.POST).is(MyControl.class).add(null);
			}
		};
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
	}

	private Method method(String methodName, Class<?>... params) throws SecurityException, NoSuchMethodException {
		return MyControl.class.getDeclaredMethod(methodName, params);
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRuleWithBothMethods() throws NoSuchMethodException {
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("/clients/add").with(HttpMethod.POST).with(HttpMethod.GET).is(MyControl.class).add(null);
			}
		};
		
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		
		assertThat(router.parse("/clients/add", HttpMethod.GET, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
	}


	class Dog {
		private Long id;

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
	}

	@br.com.caelum.vraptor.Resource
	public static class MyControl {
		public void add(Dog object) {
		}

		public void unknownMethod() {
		}

		public void list() {
		}

		public void show(Dog dog) {
		}
	}

	@Resource
	public static class MyResource {
		public void notAnnotated() {
		}

		@Path("/myPath")
		public void customizedPath() {
		}

		@Path("/*/customPath")
		public void starPath() {
		}
		
		public void withParameter(Dog dog) {
		}
		
		public void withParameter(Dog dog, String a) {
		}
	}

	@br.com.caelum.vraptor.Resource
	class InheritanceExample extends MyResource {
	}

	@Test
	public void usesAsteriskBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		final Method method = MyResource.class.getMethod("starPath");
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}
	
	private void registerRulesFor(Class<?> type) {
		RoutesParser parser = new PathAnnotationRoutesParser(router);
		
		ResourceClass resourceClass = new DefaultResourceClass(type);
		List<Route> rules = parser.rulesFor(resourceClass);
		for (Route route : rules) {
			router.add(route);
		}
	}

	@Test
	public void canTranslateAInheritedResourceBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		registerRulesFor(InheritanceExample.class);
		final Method method = MyResource.class.getMethod("notAnnotated");
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}
	
	@Test
	public void shouldReturnRouterIfMethodInheritanceMethodWithParameter() throws NoSuchMethodException, SecurityException {
		registerRulesFor(MyResource.class);
		registerRulesFor(InheritanceExample.class);
		final Method method = MyResource.class.getMethod("withParameter", Dog.class);
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}
	
	@Test
	public void shouldReturnRouterIfMethodWithParameterOver() throws NoSuchMethodException, SecurityException {
		registerRulesFor(MyResource.class);
		registerRulesFor(InheritanceExample.class);
		final Method method = MyResource.class.getMethod("withParameter", Dog.class, String.class);
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}

	@Test
	public void canTranslateAnnotatedMethodBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		final Method method = MyResource.class.getMethod("customizedPath");
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
	}
}

class MyCustomResource {
	public void notAnnotated() {
	}
}