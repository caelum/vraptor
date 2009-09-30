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

package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private ResourceClass resource;
    private HttpServletRequest request;
	private AcceptHeaderToFormat acceptHeaderToFormat;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.resource = mockery.mock(ResourceClass.class);
        this.acceptHeaderToFormat = mockery.mock(AcceptHeaderToFormat.class);
     
        mockery.checking(new Expectations(){{
        	allowing(acceptHeaderToFormat).getFormat("application/json"); will(returnValue("json"));
        	allowing(acceptHeaderToFormat).getFormat(with(any(String.class))); will(returnValue("html"));
        }});
    }
    
    @Test
    public void shouldUseResourceTypeAndMethodNameToResolveJsp() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue(null));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldUseTheFormatParameterIfSupplied() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("json"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldIgnoreHtmlFormat() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("html"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void useAcceptHeaderWhenFormatIsNotDefined() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("application/json"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue(null));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void formatParamOverridesAcceptHeader() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("application/json"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("html"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void formatParamOverridesAcceptHeader2() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("text/html"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("json"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
}
