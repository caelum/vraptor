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

import static org.junit.Assert.*;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.jmock.Expectations;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @SuppressWarnings("unchecked")
    @Test
    public void canProvidePicoSpecificApplicationScopedComponents() {
        List<Class<?>> components = Arrays.asList(Scanner.class, ComponentRegistrar.class,
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
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        configureExpectations(request);
        RequestInfo webRequest = new RequestInfo(context, request, response);
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
					URL fixtureJarURL = GenericContainerTest.class.getResource("test-fixture.jar");
					File fixtureJarFile = new File(new URI(fixtureJarURL.toString()));
					allowing(context).getRealPath("/WEB-INF/classes/");
					will(returnValue(fixtureJarFile.getAbsolutePath()));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
		}
    }
}
