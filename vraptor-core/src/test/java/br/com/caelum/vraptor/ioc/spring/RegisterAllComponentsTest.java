package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

public class RegisterAllComponentsTest extends GenericContainerTest {

    protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

    @Override
    public void setup() throws IOException {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequestMock()));
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        RequestContextHolder.resetRequestAttributes();
    }

    @Override
    protected VRaptorRequest createRequest() {
        VRaptorRequest request = super.createRequest();
        VRaptorRequestHolder.setRequestForCurrentThread(request);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request.getRequest()));
        return request;
    }
}