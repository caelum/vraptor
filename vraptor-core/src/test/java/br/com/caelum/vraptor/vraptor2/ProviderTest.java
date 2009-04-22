package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.Execution;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import org.jmock.Expectations;
import org.junit.Before;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class ProviderTest extends GenericContainerTest {
    private int counter;

    @Before
    public void setup() throws IOException {
        super.setup();
        mockery.checking(new Expectations() {
            {
                // TODO nasty, should be one()?
                allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue("non-existing-vraptor.xml"));
                allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue("views.properties"));
            }
        });
    }

    protected ContainerProvider getProvider() {
        return new Provider();
    }

    protected <T> T executeInsideRequest(Execution<T> execution) {
        HttpServletRequestMock httpRequest = new HttpServletRequestMock();
        final HttpSession session = mockery.mock(HttpSession.class, "session" + ++counter);
        httpRequest.setSession(session);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        VRaptorRequest request = new VRaptorRequest(context, httpRequest, response);
        mockery.checking(new Expectations() {
            {
                allowing(session).setAttribute("org.vraptor.scope.ScopeType_FLASH", new HashMap());
                allowing(session).getAttribute("org.vraptor.scope.ScopeType_FLASH");
                will(returnValue(new HashMap()));
            }
        });
        return execution.execute(request);
    }
}
