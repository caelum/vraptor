package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.Execution;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvidePicoSpecificApplicationScopedComponents() {
        checkAvailabilityFor(true, DirScanner.class, ResourceLocator.class);
        mockery.assertIsSatisfied();
    }

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

    protected <T> T executeInsideRequest(Execution<T> execution) {
        HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        HttpServletRequestMock httpRequest = new HttpServletRequestMock(session);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        VRaptorRequest request = new VRaptorRequest(context, httpRequest, response);
        return execution.execute(request, counter);
    }

}
