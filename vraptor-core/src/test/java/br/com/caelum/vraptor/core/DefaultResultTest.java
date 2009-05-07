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
package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.http.route.UriBasedRoute;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultResultTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.container = mockery.mock(Container.class);
    }

    public static class MyView implements View {

    }

    @Test
    public void shouldUseContainerForNewView() {
        DefaultResult result = new DefaultResult(request, container);
        final MyView expectedView = new MyView();
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyView.class);
                will(returnValue(expectedView));
            }
        });
        MyView view = result.use(MyView.class);
        MatcherAssert.assertThat(view, Matchers.is(Matchers.equalTo(expectedView)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldSetRequestAttribute() {
        DefaultResult result = new DefaultResult(request, container);
        mockery.checking(new Expectations() {
            {
                one(request).setAttribute("my_key", "my_value");
            }
        });
        result.include("my_key", "my_value");
        mockery.assertIsSatisfied();
    }

	@Test
	public void shouldAllowGroupsToBeUsedWhenDefiningTheTypeByString() {
		UriBasedRoute route = new UriBasedRoute("/(*)/(*)");
		route.is(V,"");
		assertThat(route.urlFor(client(null)), is(equalTo("/clients/")));
	}

}
