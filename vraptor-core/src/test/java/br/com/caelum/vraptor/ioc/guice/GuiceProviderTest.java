package br.com.caelum.vraptor.ioc.guice;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import javax.servlet.http.HttpSessionListener;

import net.vidageek.mirror.dsl.Mirror;

import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.fixture.CustomMethodNotAllowedHandler;
import br.com.caelum.vraptor.ioc.fixture.HasConstructor;
import br.com.caelum.vraptor.ioc.fixture.NoConstructor;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;
import br.com.caelum.vraptor.serialization.RepresentationResult;

public class GuiceProviderTest extends SpringProviderRegisteringComponentsTest {

	@Override
	protected ContainerProvider getProvider() {
		return new GuiceProvider();
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

	@Test
	public void shouldBeAbleToReceiveListsOfSerializationsAsDependency() throws Exception {
		RepresentationResult instance = getFromContainer(RepresentationResult.class);
		Collection serializations = (Collection) new Mirror().on(instance).get().field("serializations");
		assertFalse(serializations.isEmpty());
	}
	@Override
	protected void configureExpectations() {
		mockery.checking(new Expectations() {{
			allowing(context).addListener(with(any(HttpSessionListener.class)));
		}});
		super.configureExpectations();
	}

}
