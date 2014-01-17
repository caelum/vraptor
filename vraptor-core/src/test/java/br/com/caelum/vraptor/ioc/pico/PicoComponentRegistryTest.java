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
package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

public class PicoComponentRegistryTest {

	private MutablePicoContainer container;
	private PicoComponentRegistry provider;
	private @Mock MutableRequest request;
	private RequestInfo webRequest;
	private @Mock Router router;
	private @Mock HttpSession session;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
	container = new PicoBuilder().withCaching().build();
	container.addComponent(DefaultInterceptorRegistry.class);
	container.addComponent(TypeNameExtractor.class, DefaultTypeNameExtractor.class);
	container.addComponent(router);
	
	when(request.getSession()).thenReturn(session);
	when(session.getAttribute(anyString())).thenReturn(null);
	
	webRequest = new RequestInfo(null, null, request, mock(MutableResponse.class));
	provider = new PicoComponentRegistry(container, new DefaultComponentFactoryRegistry());
	provider.init();
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
	Container container = provider.provideRequestContainer(webRequest);
	Base instance = container.instanceFor(Base.class);
	assertThat(instance.getClass(), is(typeCompatibleWith(MySecondImplementation.class)));
	}

	@Test
	public void shouldRemovePreviouslyRegisteredComponentIfRegisteringAgainInAnotherScope() {
	provider.register(Base.class, MyFirstImplementation.class);
	provider.register(Base.class,AppImplementation.class);
	Container container = provider.provideRequestContainer(webRequest);
	Base instance = container.instanceFor(Base.class);
	assertThat(instance.getClass(), is(typeCompatibleWith(AppImplementation.class)));
	}

}
