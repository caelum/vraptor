package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.Execution;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvidePicoSpecificComponents() {
        Class<?>[] components = new Class[]{DirScanner.class, ResourceLocator.class};
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

    protected <T> T executeInsideRequest(Execution<T> execution) {
        HttpServletRequestMock httpRequest = new HttpServletRequestMock();
        httpRequest.setSession(mockery.mock(HttpSession.class, "session" + ++counter));
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        VRaptorRequest request = new VRaptorRequest(context, httpRequest, response);
        return execution.execute(request);
    }

}
