package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceRegistry;

public class PicoContainersProviderTest {

    private Mockery mockery;
    private MutablePicoContainer container;
    private PicoContainersProvider provider;
    private HttpServletRequest request;
    private VRaptorRequest webRequest;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = new PicoBuilder().withCaching().build();
        container.addComponent(DefaultInterceptorRegistry.class);
        final ResourceRegistry registry = mockery.mock(ResourceRegistry.class, "registry");
        container.addComponent(registry);
        this.request = mockery.mock(HttpServletRequest.class, "request");
        final HttpSession session = mockery.mock(HttpSession.class, "session");
        mockery.checking(new Expectations() {
            {
                one(registry).all(); will(returnValue(new ArrayList()));
                allowing(request).getSession(); will(returnValue(session));
                allowing(session).getAttribute(with(any(String.class))); will(returnValue(null));
                allowing(session).setAttribute(with(any(String.class)), with(any(String.class))); will(returnValue(null));
            }
        });
        this.webRequest = new VRaptorRequest(null, request, mockery.mock(HttpServletResponse.class));
        this.provider = new PicoContainersProvider(container);
    }

    interface Base {
    }

    public static class MyFirstImplementation implements Base {
    }

    public static class MySecondImplementation implements Base {
    }

    @ApplicationScoped
    public static class AppImplementation implements Base {
    }

    @Test
    public void shouldRemovePreviouslyRegisteredComponentIfRegisteringAgain() {
        provider.register(Base.class, MyFirstImplementation.class);
        provider.register(Base.class,MySecondImplementation.class);
        Container container = provider.provide(webRequest);
        Base instance = container.instanceFor(Base.class);
        assertThat(instance.getClass(), is(typeCompatibleWith(MySecondImplementation.class)));
    }

    @Test
    public void shouldRemovePreviouslyRegisteredComponentIfRegisteringAgainInAnotherScope() {
        provider.register(Base.class, MyFirstImplementation.class);
        provider.register(Base.class,AppImplementation.class);
        Container container = provider.provide(webRequest);
        Base instance = container.instanceFor(Base.class);
        assertThat(instance.getClass(), is(typeCompatibleWith(AppImplementation.class)));
    }

}
