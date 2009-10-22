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

package br.com.caelum.vraptor.converter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

public class JstlWrapperTest {

    public static final String JSTL_LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";

    private Mockery mockery;

    private ServletContext context;

    private MutableRequest request;

    private RequestInfo webRequest;

    private JstlWrapper jstlWrapper;

    private HttpSession session;

    @Before
    public void setup() {
        this.jstlWrapper = new JstlWrapper();
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
        this.request = mockery.mock(MutableRequest.class);
        this.session = mockery.mock(HttpSession.class);
        FilterChain chain = mockery.mock(FilterChain.class);
        this.webRequest = new RequestInfo(context, chain, request, null);
        mockery.checking(new Expectations() {
            {
                allowing(request).getSession();
                will(returnValue(session));
            }
        });
    }

    @Test
    public void testFindNonExistentAttribute() {
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.nonexistent.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.nonexistent.attribute.session");
                will(returnValue(null));
                exactly(1).of(context).getAttribute("my.nonexistent.attribute.application");
                will(returnValue(null));
                exactly(1).of(context).getInitParameter("my.nonexistent.attribute");
                will(returnValue(null));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.nonexistent.attribute");
        assertNull(value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInRequestContext() {
        String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(2).of(request).getAttribute("my.attribute.request");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInSessionContext() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(2).of(session).getAttribute("my.attribute.session");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInApplicationContext() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        String attributeValue = "myValue";

        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.attribute.session");
                will(returnValue(null));
                exactly(2).of(context).getAttribute("my.attribute.application");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeAsInitParameter() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        final String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.attribute.session");
                will(returnValue(null));
                exactly(1).of(context).getAttribute("my.attribute.application");
                will(returnValue(null));
                exactly(1).of(context).getInitParameter("my.attribute");
                will(returnValue(attributeValue));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

}
