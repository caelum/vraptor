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
package br.com.caelum.vraptor.vraptor2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Test;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
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

                    File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
                    File tmp = new File(tmpDir, "_tmp_vraptor_test");
                    tmp.mkdir();
                    File webInf = new File(tmp, "WEB-INF");
                    webInf.mkdir();
                    File webInfClasses = new File(webInf, "classes");
                    webInfClasses.mkdir();

                    allowing(context).getRealPath("/WEB-INF/classes/");
                    will(returnValue(webInfClasses.getAbsolutePath()));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
