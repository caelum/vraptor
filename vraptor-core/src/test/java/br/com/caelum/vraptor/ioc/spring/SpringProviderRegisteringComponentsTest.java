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

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletRequestEvent;

import org.jmock.Expectations;
import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class SpringProviderRegisteringComponentsTest extends GenericContainerTest {
    private int counter;

    @Override
	protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

    @Override
	protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
        Callable<T> task = new Callable<T>(){
			public T call() throws Exception {
				T result = null;
				HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
				HttpServletRequestMock httpRequest = new HttpServletRequestMock(session,
						mockery.mock(MutableRequest.class, "request" + counter), mockery);
				MutableResponse response = mockery.mock(MutableResponse.class, "response" + counter);

				RequestInfo request = new RequestInfo(context, null, httpRequest, response);
				VRaptorRequestHolder.setRequestForCurrentThread(request);

				RequestContextListener contextListener = new RequestContextListener();
				contextListener.requestInitialized(new ServletRequestEvent(context, httpRequest));
				result = execution.execute(request, counter);
				contextListener.requestDestroyed(new ServletRequestEvent(context, httpRequest));

				VRaptorRequestHolder.resetRequestForCurrentThread();
				return result;
			}
		};

		Future<T> future = Executors.newSingleThreadExecutor().submit(task);

		try {
			return future.get(60, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    protected void configureExpectations() {
        mockery.checking(new Expectations() {
            {
				allowing(context).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
				will(returnValue(null));

				allowing(context).getRealPath(with(any(String.class)));
				will(returnValue(SpringBasedContainer.class.getResource("../fixture").getFile()));

                allowing(context);
            }
        });
    }
}