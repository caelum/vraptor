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
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

public class BasicConfigurationTest {

    @Mock private ServletContext context;
    private BasicConfiguration config;

    @Before
    public void config() {
        MockitoAnnotations.initMocks(this);
        this.config = new BasicConfiguration(context);
    }


    @Test
    public void shouldReadRootDirectoryAsWebinfClasses() throws ServletException {
        when(context.getRealPath("/WEB-INF/classes/")).thenReturn("/x/WEB-INF/classes/");

        MatcherAssert.assertThat(config.getWebinfClassesDirectory(), is("/x/WEB-INF/classes/"));
    }


    @Test
    public void shouldUseSpringContainerAsDefaultProvider() throws ServletException {
        when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);

        MatcherAssert.assertThat(config.getProvider().getClass(), is(typeCompatibleWith(SpringProvider.class)));
    }


    @Test
    public void shouldReturnThatHasNoBasePackageWhenInitParamNull() throws ServletException {
        when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn(null);

        MatcherAssert.assertThat(config.hasBasePackages(),  is(false));
    }


    @Test
    public void shouldReturnBasePackagesWhenInitParamNotNull() throws ServletException {
        when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages");

        MatcherAssert.assertThat(config.getBasePackages(), is(new String[] {"some.packages"}));
    }

    @Test
    public void shouldReturnBasePackagesArrayWhenInitParamNotNullAndHasComas() throws ServletException {
        when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages,other.packages");

        MatcherAssert.assertThat(config.getBasePackages(), is(new String[] {"some.packages", "other.packages"}));
    }
    @Test
    public void shouldReturnBasePackagesArrayWhenInitParamNotNullAndHasComasAndSpaces() throws ServletException {
    	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages, \n      other.packages");

    	MatcherAssert.assertThat(config.getBasePackages(), is(new String[] {"some.packages", "other.packages"}));
    }
    @Test
    public void shouldReturnBasePackagesArrayWhenInitParamHasLeadingAndTrailingSpaces() throws ServletException {
    	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("    \nsome.package\n   ");

    	MatcherAssert.assertThat(config.getBasePackages(), is(new String[] {"some.package"}));
    }


    public static class MyCustomProvider implements ContainerProvider {
        public void start(ServletContext context) {
        }

        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            return execution.insideRequest(null);
        }

        public void stop() {
        }
        public Container getContainer() {
        	return null;
        }
    }

    @Test
    public void shouldAllowProviderOverriding() throws ServletException {
        when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER))
        	.thenReturn(MyCustomProvider.class.getName());

        MatcherAssert.assertThat(config.getProvider().getClass(), Matchers.is(Matchers
                .typeCompatibleWith(MyCustomProvider.class)));
    }

    public static class DogProvider implements ContainerProvider {
        public DogProvider() throws IOException {
            throw new IOException("");
        }

        public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
            return execution.insideRequest(null);
        }

        public void start(ServletContext context) {
        }

        public void stop() {
        }
        
        public Container getContainer() {
        	return null;
        }
    }

    @Test
    public void shouldThrowInstantiationExceptionCauseIfThereIsSuchException() {
        when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER))
        	.thenReturn(DogProvider.class.getName());
        try {
            config.getProvider();
            Assert.fail();
        } catch (ServletException e) {
        	Assert.assertNotNull("Should have a cause", e.getRootCause());
            Assert.assertEquals(IOException.class, e.getRootCause().getClass());
        }
    }
}
