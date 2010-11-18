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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DogController;

public class VRaptor2PathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private ResourceClass resource;
    private VRaptor2PathResolver resolver;
    private Config config;
    private HttpServletRequest request;
	private MethodInfo info;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.resource = mockery.mock(ResourceClass.class);
        this.config = mockery.mock(Config.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.info =mockery.mock(MethodInfo.class);
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept");
                one(config).getViewPattern(); will(returnValue("/$component/$logic.$result.jsp"));
            }
        });
        this.resolver = new VRaptor2PathResolver(config, request, info);
    }

    @Test
    public void shouldDelegateToVraptor3IfItsNotAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                exactly(2).of(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(DogController.class.getDeclaredMethod("bark")));
                exactly(2).of(resource).getType();
                will(returnValue(DogController.class));
            }
        });
        String result = resolver.pathFor(method);
        assertThat(result, is(equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseVRaptor2AlgorithmIfAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	one(info).getResult(); will(returnValue("ok"));
                one(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(CowLogic.class.getDeclaredMethod("eat")));
                exactly(2).of(resource).getType();
                will(returnValue(CowLogic.class));
            }
        });
        String result = resolver.pathFor(method);
        assertThat(result, is(equalTo("/cow/eat.ok.jsp")));
        mockery.assertIsSatisfied();
    }

}
