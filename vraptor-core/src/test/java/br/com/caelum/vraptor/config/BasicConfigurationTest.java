/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

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
    public void shouldUseSpringContainerAsDefaultProvider() throws ServletException {
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(BasicConfiguration.CONTAINER_PROVIDER);
                will(returnValue(null));
            }
        });
        MatcherAssert.assertThat(config.getProvider().getClass(), is(typeCompatibleWith(SpringProvider.class)));
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
