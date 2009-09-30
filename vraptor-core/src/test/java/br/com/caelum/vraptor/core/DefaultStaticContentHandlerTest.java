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
package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class DefaultStaticContentHandlerTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private ServletContext context;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.context = mockery.mock(ServletContext.class);
    }

    @Test
    public void returnsTrueForRealStaticResources() throws Exception {
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI();
                File file = File.createTempFile("_test", ".xml");
                String key = file.getAbsolutePath();
                will(returnValue("/contextName/" +key));
                one(request).getContextPath(); will(returnValue("/contextName/"));
                one(context).getResource(key); will(returnValue(file.toURI().toURL()));
            }
        });
        boolean result = new DefaultStaticContentHandler(context).requestingStaticFile(request);
        assertThat(result, is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsFalseForNonStaticResources() throws Exception {
        mockery.checking(new Expectations() {
            {
                File file = new File("_test_unknown.xml");
                String key = file.getAbsolutePath();
                one(request).getRequestURI();
                will(returnValue("/contextName/" +key));
                one(request).getContextPath(); will(returnValue("/contextName/"));
                one(context).getResource(key); will(returnValue(null));
            }
        });
        boolean result = new DefaultStaticContentHandler(context).requestingStaticFile(request);
        assertThat(result, is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

}
