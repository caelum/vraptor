/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.ioc.pico;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.NoRoutesParser;
import br.com.caelum.vraptor.proxy.DefaultProxifier;

public class PicoBasedContainerTest {

    public static class Fruit {

    }

    public static class Juice {
        private final Fruit fruit;

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
        this.picoContainer = new PicoBuilder().withCaching().build();
        this.container = new PicoBasedContainer(picoContainer, new DefaultRouter(new NoRoutesConfiguration(), new NoRoutesParser(), new DefaultProxifier(), null, null));
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
