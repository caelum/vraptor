package br.com.caelum.vraptor.ioc.guice;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;

public class GuiceProviderTest extends SpringProviderRegisteringComponentsTest {

	@Override
	protected ContainerProvider getProvider() {
		return new GuiceProvider();
	}

}
