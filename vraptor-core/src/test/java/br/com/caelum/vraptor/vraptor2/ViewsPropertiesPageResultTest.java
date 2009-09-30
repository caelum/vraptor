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

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;

public class ViewsPropertiesPageResultTest {

    private Mockery mockery;
    private ViewsPropertiesPageResult result;
    private Config config;
    private MutableRequest request;
    private PathResolver resolver;
    private ResourceMethod method;
    private HttpServletResponse response;
    private ServletContext context;
    private HttpSession session;
    private ResourceClass resource;
    private RequestDispatcher dispatcher;
	private RequestInfo webRequest;
	private MethodInfo info;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(MutableRequest.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.session = mockery.mock(HttpSession.class);
        this.method = mockery.mock(ResourceMethod.class);
        this.resource = mockery.mock(ResourceClass.class);
        this.config = mockery.mock(Config.class);
        this.resolver = mockery.mock(PathResolver.class);
        this.dispatcher = mockery.mock(RequestDispatcher.class);
        this.info = mockery.mock(MethodInfo.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getParameterMap();
                will(returnValue(new HashMap<String, Object>()));
                allowing(request).getSession();
                will(returnValue(session));
                allowing(session).getAttribute("org.vraptor.scope.ScopeType_FLASH");
                will(returnValue(new HashMap<String, Object>()));
                allowing(info).getResourceMethod();
                will(returnValue(method));
                allowing(method).getResource();
                will(returnValue(resource));
                allowing(info).getResult(); will(returnValue("ok"));
            }
        });
        this.webRequest = new RequestInfo(context, request, response);
        DefaultPageResult delegate = new DefaultPageResult(request, response, info, resolver, null, null);
        this.result = new ViewsPropertiesPageResult(this.config, this.resolver, this.info, this.webRequest, info, null, delegate);
    }

    @Component
    class CommonComponentOld {
        public void base() {
        }
    }

    @Test
    public void forwardNotOverridenUsesTheCommonAlgorithm() throws NoSuchMethodException, ServletException, IOException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue(null));
                one(resolver).pathFor(method);
                will(returnValue("defaultPath"));
                one(request).getRequestDispatcher("defaultPath");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward();
        mockery.assertIsSatisfied();
    }

    @Test
    public void redirectPrefixExecutesClientRedirection() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("redirect:clientSide"));
                one(response).sendRedirect("clientSide");
            }
        });
        this.result.forward();
        mockery.assertIsSatisfied();
    }

    class NewResource {
        public void base() {
        }
    }

    @Test
    public void newResourceUsesCommonAlgorithm() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(NewResource.class));
                allowing(method).getMethod();
                will(returnValue(NewResource.class.getMethod("base")));
                one(resolver).pathFor(method);
                will(returnValue("MyPath"));
                one(request).getRequestDispatcher("MyPath");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward();
        mockery.assertIsSatisfied();
    }

    @Test
    public void commonOverrideExecutesForward() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("serverSide"));
                one(request).getRequestDispatcher("serverSide");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward();
        mockery.assertIsSatisfied();
    }

    @Test(expected = ResultException.class)
    public void expressionProblemThrowsExceptionAndDoesNotRedirect() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("serverSide?client.id=${client.id}"));
                exactly(2).of(request).getAttribute("client"); will(returnValue(new CommonComponentOld()));
            }
        });
        this.result.forward();
        mockery.assertIsSatisfied();
    }

    @Test
    public void includesUsesTheCommonAlgorithm() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(resolver).pathFor(method);
                will(returnValue("defaultPath"));
                one(request).getRequestDispatcher("defaultPath");
                will(returnValue(dispatcher));
                one(dispatcher).include(request, response);
            }
        });
        this.result.include();
        mockery.assertIsSatisfied();
    }
}
