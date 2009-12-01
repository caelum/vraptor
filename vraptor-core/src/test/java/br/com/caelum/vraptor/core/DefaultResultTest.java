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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultResultTest {

    @Mock private HttpServletRequest request;
    @Mock private Container container;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    public static class MyView implements View {

    }

    @Test
    public void shouldUseContainerForNewView() {
        DefaultResult result = new DefaultResult(request, container);
        final MyView expectedView = new MyView();
        when(container.instanceFor(MyView.class)).thenReturn(expectedView);

        MyView view = result.use(MyView.class);
        assertThat(view, is(expectedView));
    }

    @Test
    public void shouldSetRequestAttribute() {
        DefaultResult result = new DefaultResult(request, container);

        result.include("my_key", "my_value");

        verify(request).setAttribute("my_key", "my_value");
    }

}
