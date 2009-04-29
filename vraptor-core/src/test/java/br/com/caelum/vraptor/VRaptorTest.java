package br.com.caelum.vraptor;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class VRaptorTest {

    private VRaptorMockery mockery;
    private FilterConfig config;
    private ServletContext context;
    private Container container;
    private RequestExecution execution;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
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
    public void shouldExecuteARequestUsingTheSpecifiedContainer() throws ServletException, IOException,
            VRaptorException {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI();
                will(returnValue("/unknown_file"));
                one(request).getContextPath();
                will(returnValue(""));
                one(context).getResource("/unknown_file");
                will(returnValue(null));
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
        ServletResponse response = mockery.mock(HttpServletResponse.class);
        VRaptor raptor = new VRaptor();
        raptor.init(this.config);
        raptor.doFilter(request, response, null);
        mockery.assertIsSatisfied();
    }

    public static class MyProvider implements ContainerProvider {
        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            Container container = (Container) vraptorRequest.getServletContext().getAttribute("container");
            return execution.insideRequest(container);
        }

        public void start(ServletContext context) {
        }

        public void stop() {
        }
    }

    public static class DoNothingProvider implements ContainerProvider {
        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            return execution.insideRequest(null);
        }

        public void start(ServletContext context) {
        }

        public void stop() {
        }
    }

    @Test
    public void shouldDeferToContainerIfStaticFile() throws IOException, ServletException {
        VRaptor raptor = new VRaptor();
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        final HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        final StaticContentHandler handler = mockery.mock(StaticContentHandler.class);
        final FilterChain chain = mockery.mock(FilterChain.class);
        mockery.checking(new Expectations() {
            {
                one(handler).requestingStaticFile(request);
                will(returnValue(true));
                one(handler).deferProcessingToContainer(chain, request, response);
            }
        });
        raptor.init(new DoNothingProvider(), handler);
        raptor.doFilter(request, response, chain);
        mockery.assertIsSatisfied();
    }

}
