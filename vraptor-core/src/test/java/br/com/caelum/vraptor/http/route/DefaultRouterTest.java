/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.VRaptorInfo;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultRouterTest {

    private DefaultProxifier proxifier;
	private Router router;
	private VRaptorMockery mockery;
	private VRaptorRequest request;
	private ParameterNameProvider provider;
	private TypeCreator creator;

	@org.junit.Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.request = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
		this.provider = mockery.mock(ParameterNameProvider.class);
		this.creator = mockery.mock(TypeCreator.class);
        this.proxifier = new DefaultProxifier();
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new NoRoutesCreator(), provider, proxifier, creator);
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
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/add").is(MyControl.class).add(null);
			}
		});
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		mockery.assertIsSatisfied();
	}

	@SuppressWarnings("unchecked")
	private Method method(String name, Class... types) throws SecurityException, NoSuchMethodException {
		return MyControl.class.getDeclaredMethod(name, types);
	}

	@Test
	public void usesTheFirstRegisteredRuleMatchingThePattern() throws SecurityException, NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/add").is(MyControl.class).add(null);
				routeFor("/clients/add").is(MyControl.class).list();
			}
		});
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/add").with(HttpMethod.POST).is(MyControl.class).add(null);
			}
		});
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRuleWithBothMethods() throws NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/add").with(HttpMethod.POST).with(HttpMethod.GET).is(MyControl.class).add(null);
			}
		});
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		assertThat(router.parse("/clients/add", HttpMethod.GET, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void ignoresAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/add").with(HttpMethod.GET).is(MyControl.class).add(null);
			}
		});
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(nullValue()));
		mockery.assertIsSatisfied();
	}

	@Test
	public void usesTheFirstRegisteredRuleIfDifferentCreatorsWereUsed() throws SecurityException, NoSuchMethodException {
		final ResourceMethod resourceMethod = mockery.mock(ResourceMethod.class);
		final Route customRule = new RuleForMethod(resourceMethod);
		router.add(new ListOfRoutes() {
			public List<Route> getRoutes() {
				return Arrays.asList(customRule);
			}
		});
		router.add(new Routes() {
			{
				routeFor("/clients").is(MyControl.class).list(); // if not
				// defined, any http method is allowed
			}
		});
		assertThat(router.parse("/clients", HttpMethod.POST, request), is(equalTo(resourceMethod)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients/{dog.id}").is(MyControl.class).show(null);
			}
		});
		ResourceMethod method = router.parse("/clients/45", HttpMethod.POST, request);
		assertThat(request.getParameter("dog.id"), is(equalTo("45")));
		assertThat(method, is(VRaptorMatchers.resourceMethod(method("show", Dog.class))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		router.add(new Routes() {
			{
				routeFor("/clients*").with(HttpMethod.POST).is(MyControl.class).unknownMethod();
			}
		});
		assertThat(router.parse("/clientsWhatever", HttpMethod.POST, request), is(VRaptorMatchers
				.resourceMethod(method("unknownMethod"))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void testReturnsNullIfResourceNotFound() {
		ResourceMethod method = router.parse("unknown_id", HttpMethod.POST, null);
		assertThat(method, is(Matchers.nullValue()));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldRegisterVRaptorInfoByDefault() throws SecurityException, NoSuchMethodException {
		ResourceMethod methodFound = router.parse("/is_using_vraptor", HttpMethod.GET, null);
		TypeSafeMatcher<ResourceMethod> expectedMethod = VRaptorMatchers.resourceMethod(VRaptorInfo.class
				.getMethod("info"));
		assertThat(methodFound, is(expectedMethod));
		mockery.assertIsSatisfied();
	}

	@br.com.caelum.vraptor.Resource
	class MyResource {
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
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesCreator(), provider,
				creator);
		router.register(mockery.resource(MyResource.class));
		final ResourceMethod resourceMethod = mockery.methodFor(MyResource.class, "starPath");
		final Method method = resourceMethod.getMethod();
		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(method); will(returnValue(new String[] {}));
				one(creator).typeFor(with(VRaptorMatchers.resourceMethod(method))); will(returnValue(Object.class));
			}
		});
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canTranslateAInheritedResourceBothWays() throws NoSuchMethodException {
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesCreator(), provider,
				creator);
		router.register(mockery.resource(MyResource.class));
		router.register(mockery.resource(InheritanceExample.class));
		final Method method = mockery.methodFor(MyResource.class, "notAnnotated").getMethod();
		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(method); will(returnValue(new String[] {}));
				one(creator).typeFor(with(VRaptorMatchers.resourceMethod(method))); will(returnValue(Object.class));
			}
		});
		String url = router.urlFor(InheritanceExample.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canTranslateAnnotatedMethodBothWays() throws NoSuchMethodException {
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesCreator(), provider,
				creator);
		router.register(mockery.resource(MyResource.class));
		final Method method = mockery.methodFor(MyResource.class, "customizedPath").getMethod();
		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(method); will(returnValue(new String[] {}));
				one(creator).typeFor(with(VRaptorMatchers.resourceMethod(method))); will(returnValue(Object.class));
			}
		});
		String url = router.urlFor(MyResource.class, method, new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(method)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void canAccessGenericTypeAndMethodRoute() throws NoSuchMethodException {
		this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesCreator(), provider,
				creator);
		router.add(new Routes() {{
			routeFor("--(*)--(*)").is(type("br.com.caelum.vraptor.http.route.{1}"), method("{2}"));
		}});
		ResourceMethod resourceMethod = router.parse("--MyResource--notAnnotated", HttpMethod.GET, request);
		assertThat(resourceMethod.getMethod(), is(equalTo(MyResource.class.getDeclaredMethod("notAnnotated"))));
		String url = router.urlFor(MyResource.class, resourceMethod.getMethod(), new Object[] {});
		assertThat(router.parse(url, HttpMethod.POST, null).getMethod(), is(equalTo(resourceMethod.getMethod())));
		mockery.assertIsSatisfied();
	}

}
