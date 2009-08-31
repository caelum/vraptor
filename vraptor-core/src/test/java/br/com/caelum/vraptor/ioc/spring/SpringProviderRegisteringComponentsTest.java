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

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.core.RequestInfo;
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
				HttpServletRequestMock httpRequest = new HttpServletRequestMock(session);
				HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);

				RequestInfo request = new RequestInfo(context, httpRequest, response);
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
                allowing(context).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
                will(returnValue("br.com.caelum.vraptor.ioc.fixture"));

				allowing(context).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
				will(returnValue(null));
            }
        });
    }
}