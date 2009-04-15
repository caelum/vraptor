package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;

import br.com.caelum.vraptor.core.VRaptorRequest;
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
                allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue("views.properties"));
            }
        });
    }

    protected ContainerProvider getProvider() {
        return new Provider();
    }

    protected VRaptorRequest createRequest() {
        VRaptorRequest webRequest = super.createRequest();
        final HttpSession session = webRequest.getRequest().getSession();
        mockery.checking(new Expectations() {
            {
                allowing(session).setAttribute("org.vraptor.scope.ScopeType_FLASH", new HashMap());
                allowing(session).getAttribute("org.vraptor.scope.ScopeType_FLASH"); will(returnValue(new HashMap()));
            }
        });
        return webRequest;
    }

}
