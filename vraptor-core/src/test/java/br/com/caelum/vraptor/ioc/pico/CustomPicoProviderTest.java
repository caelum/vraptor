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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.PreDestroy;

import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
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
        MutableResponse response = mockery.mock(MutableResponse.class, "response" + counter);
        RequestInfo webRequest = new RequestInfo(context, null, new HttpServletRequestMock(session, request, mockery), response);
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
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
