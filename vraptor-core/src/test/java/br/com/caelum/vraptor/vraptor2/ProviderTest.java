package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import org.jmock.Expectations;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;

public class ProviderTest extends GenericContainerTest {

    public void setup() throws IOException {
        super.setup();
        mockery.checking(new Expectations() {
            {
                // TODO nasty, should be one()?
                allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue("non-existing-vraptor.xml"));
            }
        });
    }

    protected ContainerProvider getProvider() {
        return new Provider();
    }

}
