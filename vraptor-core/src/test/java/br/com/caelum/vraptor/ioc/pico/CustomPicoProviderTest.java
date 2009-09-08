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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class CustomPicoProviderTest extends GenericContainerTest {
    private int counter;

    static class CustomPicoProvider extends PicoProvider  {
    	@Override
    	protected void registerBundledComponents(ComponentRegistry registry) {
    		super.registerBundledComponents(registry);
    		registry.register(MyAppComponent.class, MyAppComponent.class);
    		registry.register(MyAppComponentWithLifecycle.class, MyAppComponentWithLifecycle.class);
    		registry.register(MyRequestComponent.class, MyRequestComponent.class);
    		registry.register(MyFactory.class, MyFactory.class);
    	}
    }

    @ApplicationScoped
    public static class MyAppComponent {

    }

    @RequestScoped
    public static class MyRequestComponent {

    }

	@ApplicationScoped
	public static class MyFactory implements ComponentFactory<Void> {
		public int calls = 0;

		@PreDestroy
		public void preDestroy() {
			calls++;
		}

		public Void getInstance() {
			return null;
		}
	}

    @ApplicationScoped
    public static class MyAppComponentWithLifecycle {
    	public int calls = 0;

    	@PreDestroy
    	public void z() {
    		calls++;
    	}
    }

    @Test
	public void callsPredestroyExactlyOneTimeForAppScopedComponents() throws Exception {
		MyAppComponentWithLifecycle component = getFromContainer(MyAppComponentWithLifecycle.class);
		assertThat(0, is(equalTo(component.calls)));
		provider.stop();
		assertThat(1, is(equalTo(component.calls)));

		resetProvider();
	}

    @Test
    public void callsPredestroyExactlyOneTimeForAppScopedComponentFactories() throws Exception {
    	MyFactory component = getFromContainer(MyFactory.class);
    	assertThat(0, is(equalTo(component.calls)));
    	provider.stop();
    	assertThat(1, is(equalTo(component.calls)));

    	resetProvider();
    }

	@Test
    public void canProvideCustomApplicationScopedComponents() {
    	MyAppComponent component = getFromContainer(MyAppComponent.class);
        assertThat(component, is(notNullValue()));
    }

    @Test
    public void canProvideCustomRequestScopedComponents() {
        checkAvailabilityFor(false, Arrays.<Class<?>>asList(MyRequestComponent.class));
    }

    @Override
	public ContainerProvider getProvider() {
        return new CustomPicoProvider();
    }

    @Override
	protected <T> T executeInsideRequest(WhatToDo<T> execution) {
        final HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        final MutableRequest request = mockery.mock(MutableRequest.class, "request" + ++counter);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("what.ever.request.uri"));
                allowing(request).getSession(); will(returnValue(session));
                allowing(request).getParameterMap(); will(returnValue(new HashMap<Object, Object>()));
                allowing(request).getParameter("view"); will(returnValue(null));
            }
        });
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        RequestInfo webRequest = new RequestInfo(context, request, response);
        return execution.execute(webRequest, counter);
    }

    @Override
    protected void configureExpectations() {
        try {
            mockery.checking(new Expectations() {
                {
                    allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                    will(returnValue("non-existing-vraptor.xml"));
                    allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                    will(returnValue("views.properties"));

                    allowing(context).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
					will(returnValue("br.com.caelum.vraptor.ioc.fixture"));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
