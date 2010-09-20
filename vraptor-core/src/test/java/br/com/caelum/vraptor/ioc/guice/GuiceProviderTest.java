package br.com.caelum.vraptor.ioc.guice;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;

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
					}
				};
			}
		};
	}

}
