package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.NullEncodingHandler;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultApplicationTest {
	
    private VRaptorMockery mockery;
    private ServletContext context;
    private Container container;
    private RequestExecution execution;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.context = mockery.mock(ServletContext.class);
        this.container = mockery.mock(Container.class);
        this.execution = mockery.mock(RequestExecution.class);
        started =stoped= false;
    }

    @Test
    public void shouldExecuteARequestUsingTheSpecifiedContainer() throws ServletException, IOException,
            VRaptorException {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        final HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI();
                will(returnValue("/unknown_file"));
                one(request).getContextPath();
                will(returnValue(""));
                one(context).getResource("/unknown_file");
                will(returnValue(null));
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(MyProvider.class.getName()));
                one(context).getAttribute("container");
                will(returnValue(container));
                one(container).instanceFor(RequestExecution.class);
                will(returnValue(execution));
                one(execution).execute();

                one(container).instanceFor(EncodingHandler.class);
                will(returnValue(new NullEncodingHandler()));
            }
        });
        Application raptor = new DefaultApplication(context);
        raptor.start();
        raptor.parse(request, response, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldStopContainer() throws ServletException, IOException,
            VRaptorException {
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(MyProvider.class.getName()));
            }
        });
        Application raptor = new DefaultApplication(context);
        raptor.start();
        assertThat(started, is(equalTo(true)));
        assertThat(stoped, is(equalTo(false)));
        raptor.stop();
        assertThat(started, is(equalTo(false)));
        assertThat(stoped, is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    private static boolean started;
	private static boolean stoped;
    public static class MyProvider implements ContainerProvider {

		public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            Container container = (Container) vraptorRequest.getServletContext().getAttribute("container");
            return execution.insideRequest(container);
        }

        public void start(ServletContext context) {
        	started = true;
        }

        public void stop() {
        	started= false;
        	stoped = true;
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
        DefaultApplication raptor = new DefaultApplication(context);
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        final HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        final StaticContentHandler handler = mockery.mock(StaticContentHandler.class);
        final FilterChain chain = mockery.mock(FilterChain.class);
        mockery.checking(new Expectations() {
            {
                one(handler).requestingStaticFile(request);
                will(returnValue(true));
                one(handler).deferProcessingToContainer(chain, request, response);
                allowing(request).setCharacterEncoding("UTF-8");
                allowing(response).setCharacterEncoding("UTF-8");
            }
        });
        raptor.init(new DoNothingProvider(), handler);
        raptor.parse(request, response, chain);
        mockery.assertIsSatisfied();
    }

}
