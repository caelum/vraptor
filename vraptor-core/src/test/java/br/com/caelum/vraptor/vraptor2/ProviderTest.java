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

package br.com.caelum.vraptor.vraptor2;

import java.util.Arrays;
import java.util.HashMap;

import org.jmock.Expectations;
import org.junit.Test;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpSessionMock;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

public class ProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvideVRaptor2SpecificApplicationScopedComponents() {
        checkAvailabilityFor(true, Arrays.<Class<?>>asList(Config.class));
    }

    @Test
    public void canProvideVRaptor2SpecificRequestScopedComponents() {
        checkAvailabilityFor(false, Arrays.<Class<?>>asList(HibernateValidatorPluginInterceptor.class,
                ValidatorInterceptor.class, ViewInterceptor.class, ComponentInfoProvider.class,
                OutjectionInterceptor.class, AjaxInterceptor.class, ValidationErrors.class));
    }

    @Override
	protected ContainerProvider getProvider() {
        return new Provider();
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
