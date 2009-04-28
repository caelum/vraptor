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
package br.com.caelum.vraptor.converter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;

public class JstlWrapperTest {

    public static final String JSTL_LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";

    private Mockery mockery;

    private ServletContext context;

    private HttpServletRequest request;

    private VRaptorRequest webRequest;

    private JstlWrapper jstlWrapper;

    private HttpSession session;

    @Before
    public void setup() {
        this.jstlWrapper = new JstlWrapper();
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.session = mockery.mock(HttpSession.class);
        this.webRequest = new VRaptorRequest(context, request, null);
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
