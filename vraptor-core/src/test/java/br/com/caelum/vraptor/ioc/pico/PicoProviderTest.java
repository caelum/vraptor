package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ContainerProvider;

public class PicoProviderTest extends GenericProviderTest {

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

}
