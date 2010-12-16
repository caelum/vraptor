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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPathResolverTest {

    private @Mock ResourceMethod method;
    private @Mock ResourceClass resource;
	private @Mock FormatResolver formatResolver;

	private DefaultPathResolver resolver;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void config() throws Exception {
    	MockitoAnnotations.initMocks(this);

    	resolver = new DefaultPathResolver(formatResolver);
    	when(method.getResource()).thenReturn(resource);
    	when(method.getMethod()  ).thenReturn(DogController.class.getDeclaredMethod("bark"));
    	when(resource.getType()  ).thenReturn((Class) DogController.class);
    }

    @Test
    public void shouldUseResourceTypeAndMethodNameToResolveJsp(){
        when(formatResolver.getAcceptFormat()).thenReturn(null);

        String result = resolver.pathFor(method);
        assertThat(result, is("/WEB-INF/jsp/dog/bark.jsp"));
    }

    @Test
    public void shouldUseTheFormatIfSupplied() throws NoSuchMethodException {
    	when(formatResolver.getAcceptFormat()).thenReturn("json");

    	String result = resolver.pathFor(method);

        assertThat(result, is("/WEB-INF/jsp/dog/bark.json.jsp"));
    }

    @Test
    public void shouldIgnoreHtmlFormat() throws NoSuchMethodException {
    	when(formatResolver.getAcceptFormat()).thenReturn("html");

    	String result = resolver.pathFor(method);

        assertThat(result, is("/WEB-INF/jsp/dog/bark.jsp"));
    }

}
