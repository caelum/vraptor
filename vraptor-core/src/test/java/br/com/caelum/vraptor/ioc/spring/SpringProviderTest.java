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
package br.com.caelum.vraptor.ioc.spring;

import static br.com.caelum.vraptor.config.BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME;
import static br.com.caelum.vraptor.config.BasicConfiguration.SCANNING_PARAM;
import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

/**
 * @author Fabio Kung
 */
public class SpringProviderTest {
	private ServletContext servletContext;
	private HttpServletRequestMock request;
	private HttpSession session;

	@Before
	public void init() {
		servletContext = mock(ServletContext.class);

		session = new HttpSessionMock(servletContext, "session");
		request = new HttpServletRequestMock(session, mock(MutableRequest.class));

		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);
	}

	@After
	public void destroy() {
		RequestContextHolder.resetRequestAttributes();
	}
	
	@Test
	public void shouldIncludeTheApplicationContextOnTheRootApplicationContextParamIfNotSet() throws Exception {
		when(servletContext.getAttributeNames()).thenReturn(enumeration(Collections.<String> emptyList()));
		when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
			.thenReturn(null);
		
		defaultExpectations();
		
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
		
		verify(servletContext).setAttribute(eq(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE), isA(WebApplicationContext.class));
	}
	
	@Test
	public void shouldNotIncludeTheApplicationContextOnTheRootApplicationContextParamIfAlreadySet() throws Exception {
		ConfigurableWebApplicationContext ctx = mock(ConfigurableWebApplicationContext.class);

		when(servletContext.getAttributeNames()).thenReturn(enumeration(Collections.<String> emptyList()));
		when(servletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(ctx);
			
		defaultExpectations();
		
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
		
		verify(servletContext, never()).setAttribute(eq(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE), isA(WebApplicationContext.class));
	}

	@Test
	public void shouldLoadInitParameterForBasePackages() {
		when(servletContext.getAttributeNames()).thenReturn(enumeration(Collections.<String> emptyList()));
		
		defaultExpectations();
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
	}

	private void defaultExpectations() {
		when(servletContext.getInitParameter(BASE_PACKAGES_PARAMETER_NAME))
			.thenReturn("br.com.caelum.vraptor.ioc.spring.components.registrar");

		when(servletContext.getRealPath("/WEB-INF/classes"))
			.thenReturn(this.getClass().getResource(".").getPath());

		when(servletContext.getInitParameter(SCANNING_PARAM)).thenReturn("enabled");

        when(servletContext.getClassLoader())
        	.thenReturn(Thread.currentThread().getContextClassLoader());
        
    	when(servletContext.getInitParameterNames()).thenReturn(enumeration(asList(SCANNING_PARAM)));
	}
}