package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.pico.GenericProviderTest;

public class ProviderTest extends GenericProviderTest {

    protected ContainerProvider getProvider() {
        return new Provider();
    }

}
