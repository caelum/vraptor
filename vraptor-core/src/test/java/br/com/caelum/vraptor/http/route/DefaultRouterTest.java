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

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

/**
 * Those are more likely to be acceptance than unit tests.
 * @author guilherme silveira
 */
public class DefaultRouterTest {

    private Proxifier proxifier;
	private DefaultRouter router;
	private VRaptorMockery mockery;
	private VRaptorRequest request;
	private ResourceMethod method;
	private Converters converters;
	private ParameterNameProvider nameProvider;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.request = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
		this.proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
		this.method = mockery.mock(ResourceMethod.class);
		this.converters = mockery.mock(Converters.class);
		this.nameProvider = mockery.mock(ParameterNameProvider.class);

		mockery.checking(new Expectations() {{
			ignoring(nameProvider);
		}});
		this.router = new DefaultRouter(new NoRoutesConfiguration(), proxifier, new NoTypeFinder(), converters, nameProvider, new JavaEvaluator());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenNoRoutesMatchTheURI() throws Exception {
		final Route route = mockery.mock(Route.class);

		mockery.checking(new Expectations() {
			{
				allowing(route).canHandle(with(any(String.class)));
				will(returnValue(false));

				ignoring(anything());
			}
		});

		router.add(route);
		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			Assert.fail("ResourceNotFoundException is expected");
		} catch (ResourceNotFoundException e) {
			mockery.assertIsSatisfied();
		}
	}
	@Test
	public void shouldThrowMethodNotAllowedExceptionWhenNoRoutesMatchTheURIWithGivenHttpMethod() throws Exception {
		final Route route = mockery.mock(Route.class);
		mockery.checking(new Expectations() {
			{
				allowing(route).canHandle(with(any(String.class)));
				will(returnValue(true));

				allowing(route).allowedMethods();
				will(returnValue(EnumSet.of(HttpMethod.GET)));

				ignoring(anything());
			}
		});

		router.add(route);
		try {
			router.parse("any uri", HttpMethod.DELETE, request);
			Assert.fail("MethodNotAllowedException is expected");
		} catch (MethodNotAllowedException e) {
			assertThat(e.getAllowedMethods(), is((Set<HttpMethod>)EnumSet.of(HttpMethod.GET)));
			mockery.assertIsSatisfied();
		}
	}

	@Test
	public void shouldObeyPriorityOfRoutes() throws Exception {
		final Route first = mockery.mock(Route.class, "first");
		final Route second = mockery.mock(Route.class, "second");
		final Route third = mockery.mock(Route.class, "third");

		final Sequence handle = mockery.sequence("invocation");
		final Sequence allowed = mockery.sequence("allowed");

		mockery.checking(new Expectations() {
			{
				allowing(first).getPriority(); will(returnValue(Path.HIGH));
				allowing(second).getPriority(); will(returnValue(Path.DEFAULT));
				allowing(third).getPriority(); will(returnValue(Path.LOW));

				allowing(first).canHandle(with(any(String.class))); will(returnValue(false));
				inSequence(handle);
				allowing(second).canHandle(with(any(String.class))); will(returnValue(false));
				inSequence(handle);
				allowing(third).canHandle(with(any(String.class))); will(returnValue(true));
				inSequence(handle);

				EnumSet<HttpMethod> get = EnumSet.of(HttpMethod.GET);

				allowing(first).allowedMethods(); will(returnValue(get));
				inSequence(allowed);
				allowing(second).allowedMethods(); will(returnValue(get));
				inSequence(allowed);
				allowing(third).allowedMethods(); will(returnValue(get));
				inSequence(allowed);

				ignoring(anything());
			}
		});
		router.add(third);
		router.add(first);
		router.add(second);

		router.parse("anything", HttpMethod.GET, request);
		mockery.assertIsSatisfied();
	}

	@Test
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		final Route route = mockery.mock(Route.class);
		mockery.checking(new Expectations() {{
			allowing(route).canHandle("/clients/add");
			will(returnValue(true));

			allowing(route).allowedMethods();
			will(returnValue(EnumSet.of(HttpMethod.POST)));

			allowing(route).resourceMethod(request, "/clients/add");
			will(returnValue(method));

			allowing(route).getPriority();
		}});
		router.add(route);
		ResourceMethod found = router.parse("/clients/add", HttpMethod.POST, request);
		assertThat(found, is(equalTo(method)));
		mockery.assertIsSatisfied();
	}


	@Test
	public void passesTheWebMethod() throws SecurityException, NoSuchMethodException {
		final HttpMethod delete = HttpMethod.DELETE;
		final Route route = mockery.mock(Route.class);
		mockery.checking(new Expectations() {{
			allowing(route).canHandle("/clients/add");
			will(returnValue(true));

			allowing(route).allowedMethods();
			will(returnValue(EnumSet.of(delete)));

			allowing(route).resourceMethod(request, "/clients/add");
			will(returnValue(method));

			allowing(route).getPriority();
		}});
		router.add(route);
		ResourceMethod found = router.parse("/clients/add", delete, request);
		assertThat(found, is(equalTo(method)));
		mockery.assertIsSatisfied();
	}
	@Test
	public void usesTheFirstRegisteredRuleMatchingThePattern() throws SecurityException, NoSuchMethodException {
		final Route route = mockery.mock(Route.class);
		final Route second = mockery.mock(Route.class, "second");
		mockery.checking(new Expectations() {{
			allowing(route).canHandle("/clients/add"); will(returnValue(true));
			allowing(second).canHandle("/clients/add");	will(returnValue(true));

			EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

			allowing(route).allowedMethods(); will(returnValue(all));
			allowing(second).allowedMethods();	will(returnValue(all));

			one(route).resourceMethod(request, "/clients/add");
			will(returnValue(method));

			allowing(route).getPriority(); will(returnValue(Path.HIGHEST));
			allowing(second).getPriority(); will(returnValue(Path.LOWEST));
		}});
		router.add(route);
		router.add(second);
		ResourceMethod found = router.parse("/clients/add", HttpMethod.POST, request);
		assertThat(found, is(equalTo(method)));
		mockery.assertIsSatisfied();
	}
	@Test
	public void throwsExceptionIfMoreThanOneUriMatchesWithSamePriority() {
		final Route route = mockery.mock(Route.class);
		final Route second = mockery.mock(Route.class, "second");
		mockery.checking(new Expectations() {{
			allowing(route).canHandle("/clients/add"); will(returnValue(true));
			allowing(second).canHandle("/clients/add");	will(returnValue(true));

			EnumSet<HttpMethod> all = EnumSet.allOf(HttpMethod.class);

			allowing(route).allowedMethods(); will(returnValue(all));
			allowing(second).allowedMethods();	will(returnValue(all));

			allowing(route).getPriority(); will(returnValue(Path.DEFAULT));
			allowing(second).getPriority(); will(returnValue(Path.DEFAULT));
		}});
		router.add(route);
		router.add(second);
		try {
			router.parse("/clients/add", HttpMethod.POST, request);
			Assert.fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
			mockery.assertIsSatisfied();
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
		mockery.assertIsSatisfied();
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
		mockery.assertIsSatisfied();
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
	}

	@br.com.caelum.vraptor.Resource
	class InheritanceExample extends MyResource {
	}

	@Test
	public void usesAsteriskBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		final ResourceMethod resourceMethod = mockery.methodFor(MyResource.class, "starPath");
		final Method method = resourceMethod.getMethod();
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	private void registerRulesFor(Class<?> type) {
		RoutesParser parser = new PathAnnotationRoutesParser(router);

		List<Route> rules = parser.rulesFor(mockery.resource(type));
		for (Route route : rules) {
			router.add(route);
		}
	}

	@Test
	public void canTranslateAInheritedResourceBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		registerRulesFor(InheritanceExample.class);
		final Method method = mockery.methodFor(MyResource.class, "notAnnotated").getMethod();
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canTranslateAnnotatedMethodBothWays() throws NoSuchMethodException {
		registerRulesFor(MyResource.class);
		final Method method = mockery.methodFor(MyResource.class, "customizedPath").getMethod();
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	@Deprecated
	@Ignore
	public void canAccessGenericTypeAndMethodRoute() throws NoSuchMethodException, ClassNotFoundException {
		Class.forName(DefaultRouterTest.class.getPackage().getName() + ".MyCustomResource");
		new Rules(router) {
			@Override
			public void routes() {
//				routeFor("--{webLogic}--{webMethod}").is(type(DefaultRouterTest.class.getPackage().getName() + ".{webLogic}"), method("{webMethod}"));
			}
		};
		ResourceMethod resourceMethod = router.parse("--" + MyCustomResource.class.getSimpleName() + "--notAnnotated", HttpMethod.GET, request);
		final Method javaMethodFound = resourceMethod.getMethod();
		assertThat(javaMethodFound, is(equalTo(MyCustomResource.class.getDeclaredMethod("notAnnotated"))));
		String url = router.urlFor(MyCustomResource.class, javaMethodFound, new Object[] {});
		assertThat(router.parse(url, HttpMethod.GET, request).getMethod(), is(equalTo(javaMethodFound)));
		mockery.assertIsSatisfied();
	}

}
class MyCustomResource {
	public void notAnnotated() {
	}
}