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
package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorMockery;

public class VRaptorTest {
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    @Test(expected = ServletException.class)
    public void shoudlComplainIfNotInAServletEnviroment() throws IOException, ServletException {
        ServletRequest request = mockery.mock(ServletRequest.class);
        ServletResponse response = mockery.mock(ServletResponse.class);
		new VRaptor().doFilter(request, response, null);
        mockery.assertIsSatisfied();
    }
}
