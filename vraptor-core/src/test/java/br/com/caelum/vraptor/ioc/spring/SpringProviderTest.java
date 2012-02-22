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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

/**
 * @author Fabio Kung
 */
public class SpringProviderTest {
	private Mockery mockery;
	private ServletContext servletContext;
	private HttpServletRequestMock request;
	private HttpSession session;

	@Before
	public void init() {
		mockery = new Mockery();
		servletContext = mockery.mock(ServletContext.class);

		session = new HttpSessionMock(servletContext, "session");
		request = new HttpServletRequestMock(session, mockery.mock(MutableRequest.class), mockery);

		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(
				request);
		RequestContextHolder.setRequestAttributes(requestAttributes);
	}

	@After
	public void destroy() {
		mockery.assertIsSatisfied();
		RequestContextHolder.resetRequestAttributes();
	}
	
	@Test
	public void shouldIncludeTheApplicationContextOnTheRootApplicationContextParamIfNotSet() throws Exception {
		mockery.checking(new Expectations() {{
			one(servletContext).getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			will(returnValue(null));
			
			one(servletContext).setAttribute(with(equal(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)), with(any(WebApplicationContext.class)));
		}});
		
		defaultExpectations();
		
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void shouldNotIncludeTheApplicationContextOnTheRootApplicationContextParamIfAlreadySet() throws Exception {
		mockery.checking(new Expectations() {{
			ConfigurableWebApplicationContext mock = mockery.mock(ConfigurableWebApplicationContext.class);
			
			one(servletContext).getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			will(returnValue(mock));
			
			never(servletContext).setAttribute(with(equal(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)), with(any(WebApplicationContext.class)));
			
			ignoring(mock);
		}});
		
		defaultExpectations();
		
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
		
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldLoadInitParameterForBasePackages() {
		defaultExpectations();
		SpringProvider provider = new SpringProvider();
		provider.start(servletContext);
	}

	private void defaultExpectations() {
		mockery.checking(new Expectations() {
			{
				atLeast(1).of(servletContext).getInitParameter(
						BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
				will(returnValue("br.com.caelum.vraptor.ioc.spring.components.registrar"));

				allowing(servletContext).getRealPath("/WEB-INF/classes");
				will(returnValue(this.getClass().getResource(".").getPath()));

				allowing(servletContext).getAttribute(with(any(String.class)));
				will(returnValue(null));

				allowing(servletContext).setAttribute(with(any(String.class)), with(any(Object.class)));

                allowing(servletContext).getInitParameter(BasicConfiguration.SCANNING_PARAM);
                will(returnValue("enabled"));

                allowing(servletContext).getClassLoader();
                will(returnValue(Thread.currentThread().getContextClassLoader()));

                allowing(servletContext);
			}
		});
	}

}