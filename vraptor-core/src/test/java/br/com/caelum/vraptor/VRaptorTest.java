package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class VRaptorTest {

    private Mockery mockery;
    private FilterConfig config;
    private ServletContext context;
    private Container container;
    private RequestExecution execution;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.config = mockery.mock(FilterConfig.class);
        this.context = mockery.mock(ServletContext.class);
        this.container = mockery.mock(Container.class);
        this.execution = mockery.mock(RequestExecution.class);
    }

    @Test(expected = ServletException.class)
    public void shoudlComplainIfNotInAServletEnviroment() throws IOException, ServletException {
        ServletRequest request = mockery.mock(ServletRequest.class);
        ServletResponse response = mockery.mock(ServletResponse.class);
        new VRaptor().doFilter(request, response, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldExecuteARequestUsingTheSpecifiedContainer() throws ServletException, IOException {
        mockery.checking(new Expectations() {
            {
                one(config).getServletContext();
                will(returnValue(context));
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(MyProvider.class.getName()));
                one(context).getAttribute("container");
                will(returnValue(container));
                one(container).instanceFor(RequestExecution.class);
                will(returnValue(execution));
                one(execution).execute();
            }
        });
        ServletRequest request = mockery.mock(HttpServletRequest.class);
        ServletResponse response = mockery.mock(HttpServletResponse.class);
        VRaptor raptor = new VRaptor();
        raptor.init(this.config);
        raptor.doFilter(request, response, null);
        mockery.assertIsSatisfied();
    }

    public static class MyProvider implements ContainerProvider {

        public Container provide(VRaptorRequest vraptorRequest) {
            return (Container) vraptorRequest.getServletContext().getAttribute("container");
        }

        public void start(ServletContext context) {
        }

        public void stop() {
        }

    }

}
