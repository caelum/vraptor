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

import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvidePicoSpecificApplicationScopedComponents() {
        List<Class<?>> components = Arrays.asList();
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    @Override
    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

    @Override
    protected <T> T executeInsideRequest(WhatToDo<T> execution) {
        final HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        final MutableRequest request = new HttpServletRequestMock(session,
        		mockery.mock(MutableRequest.class, "request" + counter), mockery);
        MutableResponse response = mockery.mock(MutableResponse.class, "response" + counter);
        RequestInfo webRequest = new RequestInfo(context, null, request, response);
        return execution.execute(webRequest, counter);
    }

    /**
     * Children providers can set custom expectations.
     */
    @Override
    protected void configureExpectations() {
    }
}
