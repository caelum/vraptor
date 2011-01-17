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

package br.com.caelum.vraptor.http.ognl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlContext;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;

import com.google.inject.util.Types;

public class ListAccessorTest {

    private ListAccessor accessor;
    private @Mock OgnlContext context;
    private @Mock List<String> instance;
	private @Mock Converters converters;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        this.instance = new ArrayList<String>();
        when(context.getRoot()).thenReturn(instance);

        when(context.get("rootType")).thenReturn(Types.listOf(String.class));
        when(converters.to(String.class)).thenReturn((Converter) new StringConverter());
        this.accessor = new ListAccessor(converters);
    }

    @Test
    public void gettingShouldReturnNullIfIndexNotFound() throws Exception {
        Object value = accessor.getProperty(null, instance, 1);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void gettingShouldReturnValueIfIndexFound() throws Exception {
        instance.add("nothing");
        instance.add("guilherme");
        Object value = accessor.getProperty(null, instance, 1);
        assertThat(value, is(Matchers.equalTo((Object) "guilherme")));
    }

    @Test
    public void settingShouldNullifyUpToIndex() throws Exception {
        accessor.setProperty(context, instance, 1, "hello");
        assertThat(instance.get(0), is(Matchers.nullValue()));
        assertThat(instance.get(1), is(Matchers.equalTo("hello")));
    }

}
