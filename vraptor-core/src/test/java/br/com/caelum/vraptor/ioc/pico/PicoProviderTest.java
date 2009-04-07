package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericProviderTest;

public class PicoProviderTest extends GenericProviderTest {

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

}
