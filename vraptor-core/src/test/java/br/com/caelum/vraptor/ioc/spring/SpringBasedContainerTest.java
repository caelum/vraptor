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

package br.com.caelum.vraptor.ioc.spring;

import static br.com.caelum.vraptor.VRaptorMatchers.canHandle;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.spring.components.ConstructorInjection;
import br.com.caelum.vraptor.ioc.spring.components.CustomTranslator;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponent;
import br.com.caelum.vraptor.ioc.spring.components.DummyConverter;
import br.com.caelum.vraptor.ioc.spring.components.DummyImplementation;
import br.com.caelum.vraptor.ioc.spring.components.DummyInterceptor;
import br.com.caelum.vraptor.ioc.spring.components.DummyResource;
import br.com.caelum.vraptor.ioc.spring.components.Foo;
import br.com.caelum.vraptor.ioc.spring.components.LifecycleComponent;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedComponent;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedContract;
import br.com.caelum.vraptor.ioc.spring.components.SameName;
import br.com.caelum.vraptor.ioc.spring.components.SpecialImplementation;
import br.com.caelum.vraptor.scan.WebAppBootstrapFactory;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
	private SpringBasedContainer container;
	private Mockery mockery;
	private MutableRequest request;
	private HttpSessionMock session;
	private ServletContext servletContext;
	private MutableResponse response;

	@Before
	public void initContainer() {
		mockery = new Mockery();
		servletContext = mockery.mock(ServletContext.class);

		mockery.checking(new Expectations() {
			{
				allowing(servletContext).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
				will(returnValue(null));

				allowing(servletContext).getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
				will(returnValue("br.com.caelum.vraptor.ioc.spring"));

				allowing(servletContext).getRealPath(with(any(String.class)));
				will(returnValue(SpringBasedContainer.class.getResource(".").getFile()));

                allowing(servletContext).getInitParameter(BasicConfiguration.SCANNING_PARAM);
                will(returnValue("enabled"));

                allowing(servletContext).getClassLoader();
                will(returnValue(Thread.currentThread().getContextClassLoader()));

                allowing(servletContext);
			}
		});

		session = new HttpSessionMock(servletContext, "session");
		request = new HttpServletRequestMock(session, mockery.mock(MutableRequest.class), mockery);
		response = mockery.mock(MutableResponse.class);

		FilterChain chain = mockery.mock(FilterChain.class);
		VRaptorRequestHolder.setRequestForCurrentThread(new RequestInfo(servletContext, chain, request, response));
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		BasicConfiguration config = new BasicConfiguration(servletContext);
		container = new SpringBasedContainer(new DefaultSpringLocator().getApplicationContext(servletContext));
		new WebAppBootstrapFactory().create(config).configure(container);
		container.start(servletContext);
	}

	@After
	public void destroyContainer() {
		container.stop();
		container = null;
		RequestContextHolder.resetRequestAttributes();
		VRaptorRequestHolder.resetRequestForCurrentThread();
	}

	@Test
	public void twoClassesWithSameNameButDifferentPackages() throws Exception {
		SameName instance1 = container.instanceFor(SameName.class);
		br.com.caelum.vraptor.ioc.spring.components.sub.SameName instance2 = container
				.instanceFor(br.com.caelum.vraptor.ioc.spring.components.sub.SameName.class);
		assertNotNull(instance1);
		assertNotNull(instance2);
	}

	@Test
	public void shouldScanAndRegisterAnnotatedBeans() {
		DummyComponent component = container.instanceFor(DummyComponent.class);
		assertNotNull("can instantiate", component);
		assertTrue("is the right implementation", component instanceof DummyImplementation);
	}

	@Test
	public void shouldRunPostConstructMethodOfApplicationScopedComponentsAtContainerStart() {
		assertTrue("should have called init", LifecycleComponent.initialized);
	}

	static class NotRegisterd {}
	@Test
	public void shouldProvideOnlyIfBeanIsRegistered() {
		assertTrue(container.canProvide(DummyComponent.class));
		assertFalse(container.canProvide(NotRegisterd.class));
	}
	@Test
	public void shouldNotProvidePrimitiveValues() {
		assertFalse(container.canProvide(Long.class));
		assertFalse(container.canProvide(long.class));
		assertFalse(container.canProvide(long[].class));
		assertFalse(container.canProvide(Long[].class));
	}

	@Test
	public void shouldSupportOtherStereotypeAnnotations() {
		SpecialImplementation component = container.instanceFor(SpecialImplementation.class);
		assertNotNull("can instantiate", component);
	}

	@Test
	public void shouldSupportConstructorInjection() {
		ConstructorInjection component = container.instanceFor(ConstructorInjection.class);
		assertNotNull("can instantiate", component);
		assertNotNull("inject dependencies", component.getDependecy());
	}

	@Test
	public void shouldProvideCurrentHttpRequest() {
		ServletRequest httpRequest = container.instanceFor(ServletRequest.class);
		assertNotNull("can provide request", httpRequest);
	}

	@Test
	public void shouldProvideCurrentVRaptorRequest() {
		RequestInfo vraptorRequest = container.instanceFor(RequestInfo.class);
		assertNotNull("can provide request", vraptorRequest);
	}

	@Test
	public void shouldProvideServletContext() {
		ServletContext context = container.instanceFor(ServletContext.class);
		assertNotNull("can provide ServletContext", context);
	}

	@Test
	public void shouldProvideTheContainer() {
		Container itself = this.container.instanceFor(Container.class);
		assertNotNull("can provide the container", itself);
	}

	@Test
	public void shouldSupportManualRegistration() {
		this.container.register(RequestScopedContract.class, RequestScopedComponent.class);
		RequestScopedContract requestScopedContract = this.container.instanceFor(RequestScopedContract.class);
		assertNotNull("can provide manual registered components", requestScopedContract);
	}

	@Test
	public void shoudSupportCustomImplementationsForAlreadyRegisteredComponents() {
		this.container.register(UrlToResourceTranslator.class, CustomTranslator.class);
		UrlToResourceTranslator translator = this.container.instanceFor(UrlToResourceTranslator.class);
		assertThat(translator, is(notNullValue()));
		assertThat(translator, is(instanceOf(CustomTranslator.class)));

	}

	@Test
	public void shoudRegisterResourcesInRouter() {
		Router router = container.instanceFor(Router.class);
		Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(DummyResource.class, DummyResource.class
				.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
	}

	@Test
	public void shoudRegisterConvertersInConverters() {
		Converters converters = container.instanceFor(Converters.class);
		Converter<?> converter = converters.to(Foo.class);
		assertThat(converter, is(instanceOf(DummyConverter.class)));
	}

	@Test
	public void shoudRegisterInterceptorsInInterceptorRegistry() {
		InterceptorRegistry registry = container.instanceFor(InterceptorRegistry.class);
		assertThat(registry.all(), hasItem(DummyInterceptor.class));
	}
}
