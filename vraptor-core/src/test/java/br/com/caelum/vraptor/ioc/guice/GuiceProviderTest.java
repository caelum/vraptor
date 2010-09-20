package br.com.caelum.vraptor.ioc.guice;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class GuiceProviderTest extends SpringProviderRegisteringComponentsTest {

	@Override
	protected ContainerProvider getProvider() {
		//XXX remove subclass when classpath scanning is ready
		return new GuiceProvider() {
			@Override
			protected Module customModule() {
				return new AbstractModule() {
					@Override
					protected void configure() {
						GuiceComponentRegistry registry = new GuiceComponentRegistry(binder());
						registry.register(ResourceInTheClasspath.class, ResourceInTheClasspath.class);
						registry.register(ComponentFactoryInTheClasspath.class, ComponentFactoryInTheClasspath.class);
						registry.register(InterceptorInTheClasspath.class, InterceptorInTheClasspath.class);
						registry.register(ConverterInTheClasspath.class, ConverterInTheClasspath.class);

						registry.register(DependentOnSomethingFromComponentFactory.class, DependentOnSomethingFromComponentFactory.class);

						registry.register(HasConstructor.class, HasConstructor.class);
						registry.register(MethodNotAllowedHandler.class, CustomMethodNotAllowedHandler.class);
					}
				};
			}
		};
	}


	@Test
	public void shouldBeAbleToOverrideVRaptorsDefaultImplementation() throws Exception {
		MethodNotAllowedHandler handler = registerAndGetFromContainer(MethodNotAllowedHandler.class, CustomMethodNotAllowedHandler.class);
		assertThat(handler, is(instanceOf(CustomMethodNotAllowedHandler.class)));
	}
	@Test
	public void shouldBeAbleToRegisterClasses() throws Exception {
		NoConstructor instance = getFromContainer(NoConstructor.class);
		assertThat(instance, is(notNullValue()));
	}
	@Test
	public void shouldBeAbleToRegisterClassesWithDependencies() throws Exception {
		HasConstructor instance = getFromContainer(HasConstructor.class);
		assertThat(instance, is(notNullValue()));
	}

	@ApplicationScoped
	static class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

		public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
		}

	}

	static class NoConstructor {

	}

	static class HasConstructor {
		public HasConstructor(Result result) {
		}
	}
}
