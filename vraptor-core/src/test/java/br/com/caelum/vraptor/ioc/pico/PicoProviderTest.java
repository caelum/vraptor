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

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @SuppressWarnings("unchecked")
    @Test
    public void canProvidePicoSpecificApplicationScopedComponents() {
        List<Class<?>> components = Arrays.asList(Scanner.class, StereotypedComponentRegistrar.class,
                ComponentFactoryRegistrar.class, InterceptorRegistrar.class, ConverterRegistrar.class,
                ResourceRegistrar.class);
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    @Override
    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

    @Override
    protected <T> T executeInsideRequest(WhatToDo<T> execution) {
        HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        HttpServletRequestMock request = new HttpServletRequestMock(session);
        MutableResponse response = mockery.mock(MutableResponse.class, "response" + counter);
        configureExpectations(request);
        RequestInfo webRequest = new RequestInfo(context, null, request, response);
        return execution.execute(webRequest, counter);
    }

    /**
     * Children providers can set custom expectations on request.
     */
    protected void configureExpectations(HttpServletRequestMock request) {
    }

    /**
     * Children providers can set custom expectations.
     */
    @Override
    protected void configureExpectations() {
        try {
            mockery.checking(new Expectations() {
                {
                	allowing(context).getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
					will(returnValue("br.com.caelum.vraptor.ioc.fixture"));

	                allowing(context).getInitParameter(BasicConfiguration.ENCODING);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
		}
    }
}
