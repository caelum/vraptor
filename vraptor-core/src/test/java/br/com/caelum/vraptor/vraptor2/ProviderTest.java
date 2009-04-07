package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;

public class ProviderTest extends GenericContainerTest {

    protected ContainerProvider getProvider() {
        return new Provider();
    }

}
