package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;

public class PicoProviderTest extends GenericContainerTest {

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

}
