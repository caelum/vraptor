package br.com.caelum.vraptor.config;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

public class BasicConfigurationTest {

    private Mockery mockery;
    private ServletContext context;
    private BasicConfiguration config;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
        this.config = new BasicConfiguration(context);
    }

    @Test
    public void shouldUsePicoContainerAsDefaultProvider() throws ServletException {
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(null));
            }
        });
        MatcherAssert.assertThat(config.getProvider().getClass(), Matchers.is(Matchers
                .typeCompatibleWith(PicoProvider.class)));
        mockery.assertIsSatisfied();
    }

    public static class MyCustomProvider implements ContainerProvider {
        public void start(ServletContext context) {
        }

        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            return execution.insideRequest(null);
        }

        public void stop() {
        }
    }

    @Test
    public void shouldAllowProviderOverriding() throws ServletException {
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(MyCustomProvider.class.getName()));
            }
        });
        MatcherAssert.assertThat(config.getProvider().getClass(), Matchers.is(Matchers
                .typeCompatibleWith(MyCustomProvider.class)));
        mockery.assertIsSatisfied();
    }

    public static class DogProvider implements ContainerProvider {
        DogProvider() throws IOException {
            throw new IOException("");
        }

        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            return execution.insideRequest(null);
        }

        public void start(ServletContext context) {
        }

        public void stop() {
        }
    }

    @Test
    public void shouldThrowInstantiationExceptionCauseIfThereIsSuchException() {
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(DogProvider.class.getName()));
            }
        });
        try {
            config.getProvider();
            Assert.fail();
        } catch (ServletException e) {
            Assert.assertEquals(IOException.class, e.getCause().getClass());
            mockery.assertIsSatisfied();
        }
    }
}
