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
package br.com.caelum.vraptor.ioc.spring;

import static br.com.caelum.vraptor.VRaptorMatchers.canHandle;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.spring.components.ConstructorInjection;
import br.com.caelum.vraptor.ioc.spring.components.CustomTranslator;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponent;
import br.com.caelum.vraptor.ioc.spring.components.DummyConverter;
import br.com.caelum.vraptor.ioc.spring.components.DummyImplementation;
import br.com.caelum.vraptor.ioc.spring.components.DummyInterceptor;
import br.com.caelum.vraptor.ioc.spring.components.DummyResource;
import br.com.caelum.vraptor.ioc.spring.components.Foo;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedComponent;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedContract;
import br.com.caelum.vraptor.ioc.spring.components.SameName;
import br.com.caelum.vraptor.ioc.spring.components.SpecialImplementation;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    private SpringBasedContainer container;
    private Mockery mockery;
    private HttpServletRequestMock request;
    private HttpSessionMock session;
    private ServletContext servletContext;
    private HttpServletResponse response;

    @Before
    public void initContainer() {
        mockery = new Mockery();
        servletContext = mockery.mock(ServletContext.class);

		mockery.checking(new Expectations() {
			{
				allowing(servletContext).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
				will(returnValue(null));

				allowing(servletContext).getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
			}
		});

        session = new HttpSessionMock(servletContext, "session");
        request = new HttpServletRequestMock(session);
        response = mockery.mock(HttpServletResponse.class);

        VRaptorRequestHolder.setRequestForCurrentThread(new RequestInfo(servletContext, request, response));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start(servletContext);
    }

    @After
    public void destroyContainer() {
        container.stop();
        container = null;
        RequestContextHolder.resetRequestAttributes();
        VRaptorRequestHolder.resetRequestForCurrentThread();
    }

    @Test
	public void twoClassesWithSameNameButDifferentPackages() throws Exception {
		SameName instance1 = container.instanceFor(SameName.class);
		br.com.caelum.vraptor.ioc.spring.components.sub.SameName instance2 = container.instanceFor(br.com.caelum.vraptor.ioc.spring.components.sub.SameName.class);
		assertNotNull(instance1);
		assertNotNull(instance2);
	}
    @Test
    public void shouldScanAndRegisterAnnotatedBeans() {
        DummyComponent component = container.instanceFor(DummyComponent.class);
        assertNotNull("can instantiate", component);
        assertTrue("is the right implementation", component instanceof DummyImplementation);
    }

    @Test
    public void shouldSupportOtherStereotypeAnnotations() {
        SpecialImplementation component = container.instanceFor(SpecialImplementation.class);
        assertNotNull("can instantiate", component);
    }

    @Test
    public void shouldSupportConstructorInjection() {
        ConstructorInjection component = container.instanceFor(ConstructorInjection.class);
        assertNotNull("can instantiate", component);
        assertNotNull("inject dependencies", component.getDependecy());
    }

    @Test
    public void shouldProvideCurrentHttpRequest() {
        ServletRequest httpRequest = container.instanceFor(ServletRequest.class);
        assertNotNull("can provide request", httpRequest);
    }

    @Test
    public void shouldProvideCurrentVRaptorRequest() {
        RequestInfo vraptorRequest = container.instanceFor(RequestInfo.class);
        assertNotNull("can provide request", vraptorRequest);
    }

    @Test
    public void shouldProvideServletContext() {
        ServletContext context = container.instanceFor(ServletContext.class);
        assertNotNull("can provide ServletContext", context);
    }

    @Test
    public void shouldProvideTheContainer() {
        Container itself = this.container.instanceFor(Container.class);
        assertNotNull("can provide the container", itself);
    }

    @Test
    public void shouldSupportManualRegistration() {
        this.container.register(RequestScopedContract.class, RequestScopedComponent.class);
        RequestScopedContract requestScopedContract = this.container.instanceFor(RequestScopedContract.class);
        assertNotNull("can provide manual registered components", requestScopedContract);
    }

    @Test
    public void shoudSupportCustomImplementationsForAlreadyRegisteredComponents() {
        this.container.register(UrlToResourceTranslator.class, CustomTranslator.class);
        UrlToResourceTranslator translator = this.container.instanceFor(UrlToResourceTranslator.class);
        assertThat(translator, is(notNullValue()));
        assertThat(translator, is(instanceOf(CustomTranslator.class)));

    }

    @Test
    public void shoudRegisterResourcesInRouter() {
    	Router router = container.instanceFor(Router.class);
    	Matcher<Iterable<? super Route>> hasItem = hasItem(canHandle(DummyResource.class, DummyResource.class.getDeclaredMethods()[0]));
		assertThat(router.allRoutes(), hasItem);
    }

    @Test
    public void shoudRegisterConvertersInConverters() {
    	Converters converters = container.instanceFor(Converters.class);
    	Converter<?> converter = converters.to(Foo.class, container);
		assertThat(converter, is(instanceOf(DummyConverter.class)));
    }

    @Test
    public void shoudRegisterInterceptorsInInterceptorRegistry() {
    	InterceptorRegistry registry = container.instanceFor(InterceptorRegistry.class);
    	assertThat(registry.all(), hasItem(DummyInterceptor.class));
    }
}
