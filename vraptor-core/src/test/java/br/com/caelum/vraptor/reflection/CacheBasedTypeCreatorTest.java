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
package br.com.caelum.vraptor.reflection;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class CacheBasedTypeCreatorTest {

    private ResourceMethod method;
    private CacheBasedTypeCreator creator;
    private @Mock TypeCreator delegate;
	private @Mock ParameterNameProvider nameProvider;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        this.method = DefaultResourceMethod.instanceFor(TypeCreator.class, TypeCreator.class.getMethods()[0]);
        this.creator = new CacheBasedTypeCreator(delegate, nameProvider);

    }

    @Test
    public void shouldUseTheSameInstanceIfRequiredTwice() {

        creator.typeFor(method);
        creator.typeFor(DefaultResourceMethod.instanceFor(TypeCreator.class, TypeCreator.class.getMethods()[0]));

        verify(delegate).typeFor(method);
    }

}
