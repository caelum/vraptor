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

package br.com.caelum.vraptor.ioc;

import static br.com.caelum.vraptor.VRaptorMatchers.canHandle;
import static br.com.caelum.vraptor.VRaptorMatchers.hasOneCopyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath.Provided;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.scan.ScannotationComponentScannerTest;

import com.google.common.base.Objects;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 *
 * @author Guilherme Silveira
 */
public abstract class GenericContainerTest {

	protected Mockery mockery;

	protected ContainerProvider provider;

	protected ServletContext context;

	protected abstract ContainerProvider getProvider();

	protected abstract <T> T executeInsideRequest(WhatToDo<T> execution);

	protected abstract void configureExpectations();

	@Test
	public void canProvideAllApplicationScopedComponents() {
		checkAvailabilityFor(true, BaseComponents.getApplicationScoped().keySet());
		mockery.assertIsSatisfied();
	}

	@Test
	public void canProvideAllPrototypeScopedComponents() {
		checkAvailabilityFor(false, BaseComponents.getPrototypeScoped().keySet());
		mockery.assertIsSatisfied();
	}

	@Test
	public void canProvideAllRequestScopedComponents() {
		checkAvailabilityFor(false, BaseComponents.getRequestScoped().keySet());
		mockery.assertIsSatisfied();
	}

	@ApplicationScoped
	public static class MyAppComponent {

	}

	@Test
	public void processesCorrectlyAppBasedComponents() {
		checkAvailabilityFor(true, MyAppComponent.class, MyAppComponent.class);
		mockery.assertIsSatisfied();
	}
	@Test
	public void canProvideJodaTimeConverters() {
		executeInsideRequest(new WhatToDo<String>() {

			public String execute(RequestInfo request, int counter) {
				assertNotNull(getFromContainerInCurrentThread(LocalDateConverter.class, request));
				assertNotNull(getFromContainerInCurrentThread(LocalTimeConverter.class, request));

				Converters converters = getFromContainerInCurrentThread(Converters.class, request);
				assertTrue(converters.existsFor(LocalDate.class));
				assertTrue(converters.existsFor(LocalTime.class));
				return null;
			}

		});
		mockery.assertIsSatisfied();
	}

	@ApplicationScoped
	public static class MyAppComponentWithLifecycle {
		public int calls = 0;

		@PreDestroy
		public void z() {
			calls++;
		}
	}

	@Test
	public void callsPredestroyExactlyOneTime() throws Exception {
		MyAppComponentWithLifecycle component = registerAndGetFromContainer(MyAppComponentWithLifecycle.class,
				MyAppComponentWithLifecycle.class);
		assertThat(component.calls, is(0));
		provider.stop();
		assertThat(component.calls, is(1));
		provider = getProvider();
		provider.start(context); // In order to tearDown ok
	}
	
	@Test
	public void setsAnAttributeOnRequestWithTheObjectTypeName() throws Exception {
		executeInsideRequest(new WhatToDo<Void>() {
			public Void execute(final RequestInfo request, int counter) {
				return provider.provideForRequest(request, new Execution<Void>() {

					public Void insideRequest(Container container) {
						Result result = container.instanceFor(Result.class);
						HttpServletRequest request = container.instanceFor(HttpServletRequest.class);
						assertSame(result, Objects.firstNonNull(request.getAttribute("result"), request.getAttribute("defaultResult")));
						return null;
					}
				});
			}
		});
	}
	
	@Test
	public void setsAnAttributeOnSessionWithTheObjectTypeName() throws Exception {
		registerAndGetFromContainer(MySessionComponent.class, MySessionComponent.class);
		executeInsideRequest(new WhatToDo<Void>() {
			public Void execute(final RequestInfo request, int counter) {
				return provider.provideForRequest(request, new Execution<Void>() {
					
					public Void insideRequest(Container container) {
						HttpSession session = container.instanceFor(HttpSession.class);
						MySessionComponent component = container.instanceFor(MySessionComponent.class);
						assertNotNull(component);
						assertSame(component, session.getAttribute("mySessionComponent"));
						return null;
					}
				});
			}
		});
	}

	@Component
	public static class MyRequestComponent {

	}
	
	@Test
	public void processesCorrectlyRequestBasedComponents() {
		checkAvailabilityFor(false, MyRequestComponent.class, MyRequestComponent.class);
		mockery.assertIsSatisfied();
	}
	@Component
	@PrototypeScoped
	public static class MyPrototypeComponent {

	}

	@Test
	public void processesCorrectlyPrototypeBasedComponents() {
		registerAndGetFromContainer(MyPrototypeComponent.class, MyPrototypeComponent.class);
		executeInsideRequest(new WhatToDo<Object>() {
			public Object execute(RequestInfo request, int counter) {
				return provider.provideForRequest(request, new Execution<Object>() {
					public Object insideRequest(Container container) {
						ComponentRegistry registry = container.instanceFor(ComponentRegistry.class);
						registry.register(MyPrototypeComponent.class, MyPrototypeComponent.class);

						MyPrototypeComponent instance1 = container.instanceFor(MyPrototypeComponent.class);
						MyPrototypeComponent instance2 = container.instanceFor(MyPrototypeComponent.class);
						assertThat(instance1, not(sameInstance(instance2)));
						return null;
					}
				});
			}
		});
		mockery.assertIsSatisfied();
	}

	@Test
	public void supportsComponentFactoriesForCustomInstantiation() {
		// TODO the registered component is only available in the next request
		// with Pico. FIX IT!
		registerAndGetFromContainer(Container.class, TheComponentFactory.class);

		TheComponentFactory factory = registerAndGetFromContainer(TheComponentFactory.class, null);
		assertThat(factory, is(notNullValue()));

		NeedsCustomInstantiation component = registerAndGetFromContainer(NeedsCustomInstantiation.class, null);
		assertThat(component, is(notNullValue()));

		registerAndGetFromContainer(DependentOnSomethingFromComponentFactory.class,
				DependentOnSomethingFromComponentFactory.class);

		DependentOnSomethingFromComponentFactory dependent = registerAndGetFromContainer(
				DependentOnSomethingFromComponentFactory.class, null);
		assertThat(dependent, is(notNullValue()));
		assertThat(dependent.getDependency(), is(notNullValue()));
	}

	@Before
	public void setup() throws Exception {
		this.mockery = new Mockery();
		this.context = mockery.mock(ServletContext.class, "servlet context");

		mockery.checking(new Expectations() {{
			allowing(context).getMajorVersion();
			will(returnValue(3));

			allowing(context).getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
			will(returnValue("br.com.caelum.vraptor.ioc.fixture"));

            allowing(context).getRealPath("/WEB-INF/classes");
            will(returnValue(getClassDir()));

            allowing(context).getClassLoader();
            will(returnValue(new URLClassLoader(new URL[] {ScannotationComponentScannerTest.class.getResource("/test-fixture.jar")}, Thread.currentThread().getContextClassLoader())));

            allowing(context).getInitParameter(BasicConfiguration.ENCODING);
            allowing(context).getInitParameter(BasicConfiguration.SCANNING_PARAM);
            will(returnValue("enabled"));

            allowing(context).setAttribute(with(any(String.class)), with(any(Object.class)));

        }});
		configureExpectations();
		provider = getProvider();
		provider.start(context);
	}

	@After
	public void tearDown() {
		provider.stop();
		provider = null;
	}

	private <T> void checkAvailabilityFor(final boolean shouldBeTheSame, final Class<T> component,
			final Class<? super T> componentToRegister) {

		T firstInstance = registerAndGetFromContainer(component, componentToRegister);
		T secondInstance = executeInsideRequest(new WhatToDo<T>() {
			public T execute(RequestInfo request, final int counter) {
				return provider.provideForRequest(request, new Execution<T>() {
					public T insideRequest(Container secondContainer) {
						if (componentToRegister != null && !isAppScoped(secondContainer, componentToRegister)) {
							ComponentRegistry registry = secondContainer.instanceFor(ComponentRegistry.class);
							registry.register(componentToRegister, componentToRegister);
						}

						ResourceMethod secondMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
						secondContainer.instanceFor(MethodInfo.class).setResourceMethod(secondMethod);
						return secondContainer.instanceFor(component);
					}
				});

			}
		});

		checkSimilarity(component, shouldBeTheSame, firstInstance, secondInstance);
	}

	protected <T> T registerAndGetFromContainer(final Class<T> componentToBeRetrieved,
			final Class<?> componentToRegister) {
		return executeInsideRequest(new WhatToDo<T>() {
			public T execute(RequestInfo request, final int counter) {

				return provider.provideForRequest(request, new Execution<T>() {
					public T insideRequest(Container firstContainer) {
						if (componentToRegister != null) {
							ComponentRegistry registry = firstContainer.instanceFor(ComponentRegistry.class);
							registry.register(componentToRegister, componentToRegister);
						}
						ResourceMethod firstMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
						firstContainer.instanceFor(MethodInfo.class).setResourceMethod(firstMethod);
						return firstContainer.instanceFor(componentToBeRetrieved);
					}
				});

			}
		});
	}

	public <T> T getFromContainer(final Class<T> componentToBeRetrieved) {
		return executeInsideRequest(new WhatToDo<T>() {
			public T execute(RequestInfo request, final int counter) {
				return getFromContainerInCurrentThread(componentToBeRetrieved, request);
			}
		});
	}

	private <T> T getFromContainerInCurrentThread(final Class<T> componentToBeRetrieved, RequestInfo request) {
		return provider.provideForRequest(request, new Execution<T>() {
			public T insideRequest(Container firstContainer) {
				return firstContainer.instanceFor(componentToBeRetrieved);
			}
		});
	}

	private boolean isAppScoped(Container secondContainer, Class<?> componentToRegister) {
		return secondContainer.instanceFor(componentToRegister) != null;
	}

	private void checkSimilarity(Class<?> component, boolean shouldBeTheSame, Object firstInstance,
			Object secondInstance) {
		if (shouldBeTheSame) {
			MatcherAssert.assertThat("Should be the same instance for " + component.getName(), firstInstance,
					is(equalTo(secondInstance)));
		} else {
			MatcherAssert.assertThat("Should not be the same instance for " + component.getName(), firstInstance,
					is(not(equalTo(secondInstance))));
		}
	}

	protected void checkAvailabilityFor(boolean shouldBeTheSame, Collection<Class<?>> components) {
		for (Class<?> component : components) {
			checkAvailabilityFor(shouldBeTheSame, component, null);
		}
	}

	@Component
	static public class DisposableComponent {
		private boolean destroyed;

		@PreDestroy
		public void preDestroy() {
			this.destroyed = true;
		}
	}

	@Component
	static public class StartableComponent {
		private boolean started;

		@PostConstruct
		public void postConstruct() {
			this.started = true;
		}
	}

	@Test
	public void shouldDisposeAfterRequest() {
		registerAndGetFromContainer(Container.class, DisposableComponent.class);
		DisposableComponent comp = registerAndGetFromContainer(DisposableComponent.class, null);
		assertTrue(comp.destroyed);
	}

	@Test
	public void shouldStartBeforeRequestExecution() {
		registerAndGetFromContainer(Container.class, StartableComponent.class);
		StartableComponent comp = registerAndGetFromContainer(StartableComponent.class, null);
		assertTrue(comp.started);
	}

	@Test
	public void canProvideComponentsInTheClasspath() throws Exception {
		checkAvailabilityFor(false, Collections.<Class<?>> singleton(CustomComponentInTheClasspath.class));
	}

	@Test
	public void shoudRegisterResourcesInRouter() {
		Router router = getFromContainer(Router.class);
		Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(ResourceInTheClasspath.class,
				ResourceInTheClasspath.class.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
	}

	@Test
	public void shoudUseComponentFactoriesInTheClasspath() {
		Provided object = getFromContainer(Provided.class);
		assertThat(object, is(sameInstance(ComponentFactoryInTheClasspath.PROVIDED)));
	}

	@Test
	public void shoudRegisterInterceptorsInInterceptorRegistry() {
		InterceptorRegistry registry = getFromContainer(InterceptorRegistry.class);
		assertThat(registry.all(), hasOneCopyOf(InterceptorInTheClasspath.class));
	}

	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentsScannedFromTheClasspath() {
		CustomComponentWithLifecycleInTheClasspath component = getFromContainer(CustomComponentWithLifecycleInTheClasspath.class);
		assertThat(component.callsToPreDestroy, is(equalTo(0)));
		provider.stop();
		assertThat(component.callsToPreDestroy, is(equalTo(1)));

		resetProvider();
	}

	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentFactoriesScannedFromTheClasspath() {
		ComponentFactoryInTheClasspath componentFactory = getFromContainer(ComponentFactoryInTheClasspath.class);
		assertThat(componentFactory.callsToPreDestroy, is(equalTo(0)));
		provider.stop();
		assertThat(componentFactory.callsToPreDestroy, is(equalTo(1)));

		resetProvider();
	}

	@Test
	public void shoudRegisterConvertersInConverters() {
		executeInsideRequest(new WhatToDo<Converters>() {
			public Converters execute(RequestInfo request, final int counter) {
				return provider.provideForRequest(request, new Execution<Converters>() {
					public Converters insideRequest(Container container) {
						Converters converters = container.instanceFor(Converters.class);
						Converter<?> converter = converters.to(Void.class);
						assertThat(converter, is(instanceOf(ConverterInTheClasspath.class)));
						return null;
					}
				});
			}
		});
	}

	protected void resetProvider() {
		provider = getProvider();
		provider.start(context);
	}

	protected String getClassDir() {
		return getClass().getResource("/br/com/caelum/vraptor/test").getFile();
	}

}
