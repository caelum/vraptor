package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.File;

public class PicoProviderTest extends GenericContainerTest {

    @Test
    public void canProvidePicoSpecificComponents() {
        Class<?>[] components = new Class[]{DirScanner.class, ResourceLocator.class};
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

}
