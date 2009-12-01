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
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultStaticContentHandlerTest {

    @Mock private HttpServletRequest request;
    @Mock private ServletContext context;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsTrueForRealStaticResources() throws Exception {
    	File file = File.createTempFile("_test", ".xml");
    	String key = file.getAbsolutePath();
    	when(request.getRequestURI()).thenReturn("/contextName/" +key);
        when(request.getContextPath()).thenReturn("/contextName/");
        when(context.getResource(key)).thenReturn(file.toURI().toURL());

        boolean result = new DefaultStaticContentHandler(context).requestingStaticFile(request);

        assertThat(result, is(equalTo(true)));
    }

    @Test
    public void returnsFalseForNonStaticResources() throws Exception {
        File file = new File("_test_unknown.xml");
        String key = file.getAbsolutePath();
        when(request.getRequestURI()).thenReturn("/contextName/" +key);
        when(request.getContextPath()).thenReturn("/contextName/");
        when(context.getResource(key)).thenReturn(null);

        boolean result = new DefaultStaticContentHandler(context).requestingStaticFile(request);

        assertThat(result, is(equalTo(false)));
    }

}
