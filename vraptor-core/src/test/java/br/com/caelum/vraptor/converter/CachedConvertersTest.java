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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class CachedConvertersTest {

    private Mockery mockery;
    private CachedConverters converters;
    private Converters delegate;
    private Converter converter;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(Converters.class);
        this.converter = mockery.mock(Converter.class);
        this.converters = new CachedConverters(delegate);
        this.container = mockery.mock(Container.class);
        mockery.checking(new Expectations() {
            {
                one(delegate).to(CachedConvertersTest.class, container); will(returnValue(converter));
            }
        });
    }

    @Test
    public void shouldUseTheProvidedConverterDuringFirstRequest() {
        Converter found = converters.to(CachedConvertersTest.class, container);
        assertThat(found, is(equalTo(this.converter)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameConverterOnFurtherRequests() {
        mockery.checking(new Expectations(){{
            one(container).instanceFor(converter.getClass()); will(returnValue(converter));
        }});
        Converter found = converters.to(CachedConvertersTest.class, container);
        assertThat(converters.to(CachedConvertersTest.class, container), is(equalTo(found)));
        mockery.assertIsSatisfied();
    }

}
