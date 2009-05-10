package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.NoRoutesParser;

public class PicoBasedContainerTest {

    public static class Fruit {

    }

    public static class Juice {
        private Fruit fruit;

        public Juice(Fruit f) {
            this.fruit = f;
        }
    }

    private Mockery mockery;
    private PicoBasedContainer container;
    private MutablePicoContainer picoContainer;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        final MutableRequest webRequest = mockery.mock(MutableRequest.class);
        final HttpServletResponse webResponse = mockery.mock(HttpServletResponse.class);
        final RequestInfo request = new RequestInfo(null, webRequest, webResponse);
        this.picoContainer = new PicoBuilder().withCaching().build();
        this.container = new PicoBasedContainer(picoContainer, new DefaultRouter(new NoRoutesConfiguration(), new NoRoutesParser(), null, null, null));
    }

    @Test
    public void shouldBeAbleToInstantiateABean() {
        this.picoContainer.addComponent(Fruit.class);
        MatcherAssert.assertThat(container.instanceFor(Fruit.class), Matchers.is(Matchers.notNullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToInstantiateADependentBean() {
        this.picoContainer.addComponent(Fruit.class);
        this.picoContainer.addComponent(Juice.class);
        Fruit fruit = container.instanceFor(Fruit.class);
        Juice juice = container.instanceFor(Juice.class);
        MatcherAssert.assertThat(juice, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(juice.fruit, Matchers.is(Matchers.equalTo(fruit)));
        mockery.assertIsSatisfied();
    }

}
