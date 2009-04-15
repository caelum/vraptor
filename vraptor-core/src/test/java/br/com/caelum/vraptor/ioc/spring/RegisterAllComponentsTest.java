package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RegisterAllComponentsTest extends GenericContainerTest {
    private RequestContextListener requestListener;
    private Mockery mockery;
    private ServletContext servletContext;
    private HttpServletRequest httpServletRequest;


    @Before
    public void setup() throws IOException {
        requestListener = new RequestContextListener();
        mockery = new Mockery();
        servletContext = mockery.mock(ServletContext.class);
        httpServletRequest = new HttpServletRequestMock();
        requestListener.requestInitialized(new ServletRequestEvent(servletContext, httpServletRequest));
        super.setup();
    }

    @After
    public void tearDown() {
        super.tearDown();
        requestListener.requestDestroyed(new ServletRequestEvent(servletContext, httpServletRequest));
    }

    protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

}