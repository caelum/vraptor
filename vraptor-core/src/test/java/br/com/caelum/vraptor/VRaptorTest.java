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
package br.com.caelum.vraptor;

 import static br.com.caelum.vraptor.config.BasicConfiguration.CONTAINER_PROVIDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.DefaultStaticContentHandler;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.NullEncodingHandler;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class VRaptorTest {

	private @Mock FilterConfig config;
	private @Mock ServletContext context;
	private @Mock static Container container;
	private @Mock RequestExecution execution;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	started = stoped = false;
	}

	@Test(expected = ServletException.class)
	public void shoudlComplainIfNotInAServletEnviroment() throws IOException, ServletException {
	ServletRequest request = mock(ServletRequest.class);
	ServletResponse response = mock(ServletResponse.class);
	
	new VRaptor().doFilter(request, response, null);
	}

	@Test
	public void shouldExecuteARequestUsingTheSpecifiedContainer() throws ServletException, IOException,
		VRaptorException {
	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);
	
	when(request.getRequestURI()).thenReturn("/unknown_file");
	when(request.getContextPath()).thenReturn("");
	when(context.getResource("/unknown_file")).thenReturn(null);
	when(config.getServletContext()).thenReturn(context);
	when(context.getInitParameter(BasicConfiguration.CONTAINER_PROVIDER)).thenReturn(MyProvider.class.getName());
	
	when(context.getAttribute("container")).thenReturn(container);
	when(container.instanceFor(RequestExecution.class)).thenReturn(execution);
	
	when(container.instanceFor(StaticContentHandler.class)).thenReturn(new DefaultStaticContentHandler(context));
	
	when(container.instanceFor(EncodingHandler.class)).thenReturn(new NullEncodingHandler());
		
	VRaptor vraptor = new VRaptor();
	vraptor.init(this.config);
	vraptor.doFilter(request, response, null);
	
	verify(execution, times(1)).execute();
	}

	@Test
	public void shouldStopContainer() throws ServletException, IOException,
		VRaptorException {
	when(config.getServletContext()).thenReturn(context);
	when(context.getInitParameter(CONTAINER_PROVIDER)).thenReturn(MyProvider.class.getName());
	when(container.instanceFor(StaticContentHandler.class)).thenReturn(new DefaultStaticContentHandler(context));
	
	VRaptor vraptor = new VRaptor();
	vraptor.init(this.config);
	assertThat(started, is(equalTo(true)));
	assertThat(stoped, is(equalTo(false)));
	
	vraptor.destroy();
	assertThat(started, is(equalTo(false)));
	assertThat(stoped, is(equalTo(true)));
	}

	private static boolean started;
	private static boolean stoped;
	public static class MyProvider implements ContainerProvider {
		
		public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
		Container container = (Container) vraptorRequest.getServletContext().getAttribute("container");
		return execution.insideRequest(container);
	}

	public void start(ServletContext context) {
		started = true;
	}

	public void stop() {
		started= false;
		stoped = true;
	}
	public Container getContainer() {
		return container;
	}
	}

	public static class DoNothingProvider implements ContainerProvider {
	private final StaticContentHandler handler;

		public DoNothingProvider(StaticContentHandler handler) {
			this.handler = handler;
		}

		public <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution) {
		return execution.insideRequest(null);
	}

	public void start(ServletContext context) {
	}

	public void stop() {
	}
	
	public Container getContainer() {
		return new Container() {
				
				public <T> T instanceFor(Class<T> type) {
					return type.cast(handler);
				}
				
				public <T> boolean canProvide(Class<T> type) {
					return false;
				}
			};
	}
	}

	@Test
	public void shouldDeferToContainerIfStaticFile() throws IOException, ServletException {
	VRaptor vraptor = new VRaptor();
	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);
	StaticContentHandler handler = mock(StaticContentHandler.class);
	FilterChain chain = mock(FilterChain.class);
	
	when(handler.requestingStaticFile(request)).thenReturn(true);
	
	vraptor.init(new DoNothingProvider(handler));
	vraptor.doFilter(request, response, chain);
	
	verify(handler, times(1)).deferProcessingToContainer(chain, request, response);
	}
}
