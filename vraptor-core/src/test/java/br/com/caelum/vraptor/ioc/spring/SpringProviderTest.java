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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caelum.vraptor.config.BasicConfiguration;
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
    private HttpServletResponse response;

    @Before
    public void init() {
        mockery = new Mockery();
        servletContext = mockery.mock(ServletContext.class);

        session = new HttpSessionMock(servletContext, "session");
        request = new HttpServletRequestMock(session);
        response = mockery.mock(HttpServletResponse.class);

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @After
    public void destroy() {
        mockery.assertIsSatisfied();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldLoadInitParameterForBasePackages() {
        mockery.checking(new Expectations() {{
            atLeast(1).of(servletContext).getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
            will(returnValue("br.com.caelum.vraptor.ioc.spring.components.registrar"));
            allowing(servletContext);will(returnValue(null));
        }});
        SpringProvider provider = new SpringProvider();
        provider.start(servletContext);
    }

}