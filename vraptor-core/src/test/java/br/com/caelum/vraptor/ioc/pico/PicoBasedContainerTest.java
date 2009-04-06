package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;

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

    @Before
    public void setup() {
        this.mockery = new Mockery();
        final HttpServletRequest webRequest = mockery.mock(HttpServletRequest.class);
        final HttpServletResponse webResponse = mockery.mock(HttpServletResponse.class);
        final VRaptorRequest request = new VRaptorRequest(null, webRequest, webResponse);
        this.container = new PicoBasedContainer(null, request, new DefaultResourceRegistry(null));
    }

    @Test
    public void shouldBeAbleToInstantiateABean() {
        this.container.register(Fruit.class);
        MatcherAssert.assertThat(container.instanceFor(Fruit.class), Matchers.is(Matchers.notNullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToInstantiateADependentBean() {
        this.container.register(Fruit.class);
        this.container.register(Juice.class);
        Fruit fruit = container.instanceFor(Fruit.class);
        Juice juice = container.instanceFor(Juice.class);
        MatcherAssert.assertThat(juice, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(juice.fruit, Matchers.is(Matchers.equalTo(fruit)));
        mockery.assertIsSatisfied();
    }

}
