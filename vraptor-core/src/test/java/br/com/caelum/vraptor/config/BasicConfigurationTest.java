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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.picocontainer.PicoContainer;
import org.springframework.context.ApplicationContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.guice.GuiceProvider;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import br.com.caelum.vraptor.ioc.spring.MissingConfigurationException;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

import com.google.inject.Guice;

public class BasicConfigurationTest {

	@Mock private ServletContext context;
	private BasicConfiguration config;

	@Before
	public void config() {
	MockitoAnnotations.initMocks(this);
	config = new BasicConfiguration(context);
	}


	@Test
	public void shouldReadRootDirectoryAsWebinfClasses() throws ServletException {
	when(context.getRealPath("/WEB-INF/classes/")).thenReturn("/x/WEB-INF/classes/");

	assertThat(config.getWebinfClassesDirectory(), is("/x/WEB-INF/classes/"));
	}


	@Test
	public void shouldUseSpringContainerAsDefaultProvider() throws ServletException {
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);

	assertThat(config.getProvider().getClass(), is(typeCompatibleWith(SpringProvider.class)));
	}

	@Test
	public void shouldUseGuiceAs1stAlternative() throws ServletException {
	BasicConfiguration configSpy = spy(config);
	
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);
		doReturn(false).when(configSpy).classExists(ApplicationContext.class.getName());
		doReturn(true).when(configSpy).classExists(Guice.class.getName());

	assertThat(configSpy.getProvider().getClass(), is(typeCompatibleWith(GuiceProvider.class)));
	}
	
	@Test
	public void shouldUsePicoAs2ndAlternative() throws ServletException {
	BasicConfiguration configSpy = spy(config);
	
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);
		doReturn(false).when(configSpy).classExists(ApplicationContext.class.getName());
		doReturn(false).when(configSpy).classExists(Guice.class.getName());
		doReturn(true).when(configSpy).classExists(PicoContainer.class.getName());

	assertThat(configSpy.getProvider().getClass(), is(typeCompatibleWith(PicoProvider.class)));
	}

	@Test
	public void shouldThrowExceptionWhenProviderClassWasNotFound() throws ServletException {
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn("UnknowClass");
	
	try {
		config.getProvider();
		fail();
	} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), startsWith("You must configure"));
		}
	}
	
	@Test
	public void shouldThrowIllegalArgumentExceptionWhenProvidersWasNotFound() throws Exception {
		BasicConfiguration configSpy = spy(config);
		
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);
		doReturn(false).when(configSpy).classExists(anyString());
		
	try {
		configSpy.getProvider();
		fail();
	} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), startsWith("You don't have any DI container jars on your classpath."));
		}
	}

	@Test
	public void shouldReturnThatHasNoBasePackageWhenInitParamNull() throws ServletException {
	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn(null);

	assertThat(config.hasBasePackages(), is(false));
	}
	
	@Test(expected = MissingConfigurationException.class)
	public void shouldThrowMissingConfigurationExceptionIfHasNoBasePackages() {
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(null);
		config.getBasePackages();
	}
	
	@Test
	public void testIfClasspathScanningIsEnabled() {
	when(context.getInitParameter(BasicConfiguration.SCANNING_PARAM)).thenReturn(null, "disabled");
	
		assertThat(config.isClasspathScanningEnabled(), is(true));
		assertThat(config.isClasspathScanningEnabled(), is(false));
	}
	
	@Test
	public void testIfHasBasePackages() {
	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn(null, "foo.bar");
	
		assertThat(config.hasBasePackages(), is(false));
		assertThat(config.hasBasePackages(), is(true));
	}

	@Test
	public void shouldReturnBasePackagesWhenInitParamNotNull() throws ServletException {
	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages");

	assertThat(config.getBasePackages(), is(new String[] {"some.packages"}));
	}

	@Test
	public void shouldReturnBasePackagesArrayWhenInitParamNotNullAndHasComas() throws ServletException {
	when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages,other.packages");

	assertThat(config.getBasePackages(), is(new String[] {"some.packages", "other.packages"}));
	}
	
	@Test
	public void shouldReturnBasePackagesArrayWhenInitParamNotNullAndHasComasAndSpaces() throws ServletException {
		when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("some.packages, \n	  other.packages");

		assertThat(config.getBasePackages(), is(new String[] {"some.packages", "other.packages"}));
	}
	
	@Test
	public void shouldReturnBasePackagesArrayWhenInitParamHasLeadingAndTrailingSpaces() throws ServletException {
		when(context.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME)).thenReturn("	\nsome.package\n   ");

		assertThat(config.getBasePackages(), is(new String[] {"some.package"}));
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

	assertThat(config.getProvider().getClass(), Matchers.is(Matchers
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
		fail();
	} catch (ServletException e) {
		assertNotNull("Should have a cause", e.getRootCause());
		assertEquals(IOException.class, e.getRootCause().getClass());
	}
	}
}
