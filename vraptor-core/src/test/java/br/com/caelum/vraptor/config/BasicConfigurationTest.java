package br.com.caelum.vraptor.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;

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
        public Container provide(VRaptorRequest vraptorRequest) {
            return null;
        }

        public void start() {
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
}
