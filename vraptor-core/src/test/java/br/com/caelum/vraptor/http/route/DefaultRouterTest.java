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

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

/**
 * Those are more likely to be acceptance than unit tests.
 * @author guilherme silveira
 */
public class DefaultRouterTest {

	private DefaultProxifier proxifier;
	private DefaultRouter router;
	private VRaptorMockery mockery;
	private VRaptorRequest request;
	private TypeCreator creator;
	private ResourceMethod method;

	@org.junit.Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.request = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
		this.creator = mockery.mock(TypeCreator.class);
		this.proxifier = new DefaultProxifier();
		this.method = mockery.mock(ResourceMethod.class);
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesParser(proxifier, new NoTypeFinder()),
				proxifier, creator, new NoTypeFinder());
	}

	@Test
	public void shouldObeyPriorityOfRoutes() throws Exception {
		final Route first = mockery.mock(Route.class, "first");
		final Route second = mockery.mock(Route.class, "second");
		final Route third = mockery.mock(Route.class, "third");

		final Sequence invocation = mockery.sequence("invocation");

		mockery.checking(new Expectations() {
			{
				allowing(first).getPriority(); will(returnValue(1));
				allowing(second).getPriority(); will(returnValue(2));
				allowing(third).getPriority(); will(returnValue(3));

				one(first).canHandle(with(any(String.class)), with(any(HttpMethod.class))); will(returnValue(false));
				inSequence(invocation);
				one(second).canHandle(with(any(String.class)), with(any(HttpMethod.class))); will(returnValue(false));
				inSequence(invocation);
				one(third).canHandle(with(any(String.class)), with(any(HttpMethod.class))); will(returnValue(false));
				inSequence(invocation);

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
			one(route).canHandle("/clients/add", HttpMethod.POST);
			will(returnValue(true));

			one(route).matches("/clients/add", HttpMethod.POST, request);
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
			one(route).canHandle("/clients/add", delete);
			will(returnValue(true));

			one(route).matches("/clients/add", delete, request);
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
			one(route).canHandle("/clients/add", HttpMethod.POST); will(returnValue(true));
			one(second).canHandle("/clients/add", HttpMethod.POST);	will(returnValue(true));

			one(route).matches("/clients/add", HttpMethod.POST, request);
			will(returnValue(method));

			allowing(route).getPriority(); will(returnValue(1));
			allowing(second).getPriority(); will(returnValue(2));
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
			one(route).canHandle("/clients/add", HttpMethod.POST); will(returnValue(true));
			one(second).canHandle("/clients/add", HttpMethod.POST);	will(returnValue(true));

			allowing(route).getPriority(); will(returnValue(1));
			allowing(second).getPriority(); will(returnValue(1));
		}});
		router.add(route);
		router.add(second);
		try {
			router.parse("/clients/add", HttpMethod.POST, request);
			Assert.fail("Exception expected");
		} catch (VRaptorException e) {
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

	@Test
	public void testReturnsNullIfResourceNotFound() {
		ResourceMethod method = router.parse("unknown_id", HttpMethod.POST, null);
		assertThat(method, is(Matchers.nullValue()));
		mockery.assertIsSatisfied();
	}

	@br.com.caelum.vraptor.Resource
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
		router.register(mockery.resource(MyResource.class));
		final ResourceMethod resourceMethod = mockery.methodFor(MyResource.class, "starPath");
		final Method method = resourceMethod.getMethod();
		allowParametersCreation(method);
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	private void allowParametersCreation(final Method method) {
		mockery.checking(new Expectations() {
			{
				one(creator).instanceWithParameters(with(VRaptorMatchers.resourceMethod(method)), with(any(Object[].class)));
				will(returnValue(new Object()));
			}
		});
	}

	@Test
	public void canTranslateAInheritedResourceBothWays() throws NoSuchMethodException {
		router.register(mockery.resource(MyResource.class));
		router.register(mockery.resource(InheritanceExample.class));
		final Method method = mockery.methodFor(MyResource.class, "notAnnotated").getMethod();
		allowParametersCreation(method);
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canTranslateAnnotatedMethodBothWays() throws NoSuchMethodException {
		router.register(mockery.resource(MyResource.class));
		final Method method = mockery.methodFor(MyResource.class, "customizedPath").getMethod();
		allowParametersCreation(method);
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canAccessGenericTypeAndMethodRoute() throws NoSuchMethodException, ClassNotFoundException {
		Class.forName(DefaultRouterTest.class.getPackage().getName() + ".MyCustomResource");
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("--{webLogic}--{webMethod}").is(type(DefaultRouterTest.class.getPackage().getName() + ".{webLogic}"), method("{webMethod}"));
			}
		};
		ResourceMethod resourceMethod = router.parse("--" + MyCustomResource.class.getSimpleName() + "--notAnnotated", HttpMethod.GET, request);
		final Method javaMethodFound = resourceMethod.getMethod();
		assertThat(javaMethodFound, is(equalTo(MyCustomResource.class.getDeclaredMethod("notAnnotated"))));
		allowParametersCreation(javaMethodFound);
		String url = router.urlFor(MyCustomResource.class, javaMethodFound, new Object[] {});
		assertThat(router.parse(url, HttpMethod.GET, request).getMethod(), is(equalTo(javaMethodFound)));
		mockery.assertIsSatisfied();
	}

}
class MyCustomResource {
	public void notAnnotated() {
	}
}