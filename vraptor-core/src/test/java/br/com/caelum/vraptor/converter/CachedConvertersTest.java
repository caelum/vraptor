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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    private Converter<?> converter;
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
		@SuppressWarnings("unchecked")
        Converter found = converters.to(CachedConvertersTest.class, container);
        assertThat(found, is(equalTo(this.converter)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameConverterOnFurtherRequests() {
        mockery.checking(new Expectations(){{
            one(container).instanceFor(converter.getClass()); will(returnValue(converter));
        }});
        assertSame(converters.to(CachedConvertersTest.class, container), converters.to(CachedConvertersTest.class, container));
        mockery.assertIsSatisfied();
    }

    @Test
	public void existsForWillReturnTrueIfTypeIsAlreadyCached() throws Exception {
    	mockery.checking(new Expectations(){{
    		one(container).instanceFor(converter.getClass()); will(returnValue(converter));

    		allowing(delegate).existsFor(CachedConvertersTest.class);
    		will(returnValue(true));
    	}});

    	assertTrue(converters.existsFor(CachedConvertersTest.class));
	}

    @Test
    public void existsForWillReturnTrueIfDelegateAlsoReturnsTrue() throws Exception {
    	mockery.checking(new Expectations(){{
    		atLeast(1).of(delegate).existsFor(CachedConvertersTest.class);
    		will(returnValue(true));
    	}});

    	assertTrue(converters.existsFor(CachedConvertersTest.class));
    }

}
