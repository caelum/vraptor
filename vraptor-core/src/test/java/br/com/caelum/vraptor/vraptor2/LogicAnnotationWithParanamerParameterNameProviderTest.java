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
package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.http.ParameterNameProvider;

public class LogicAnnotationWithParanamerParameterNameProviderTest {

    class Component {
        @Logic(parameters = { "first" })
        public void annotated(String value) {
        }

        public void nonAnnotated(String value) {
        }
    }

    @Test
    public void shouldUseParametersWithAnnotation() throws SecurityException, NoSuchMethodException {
        String[] result = new LogicAnnotationWithParanamerParameterNameProvider().parameterNamesFor(Component.class
                .getMethod("annotated", String.class));
        assertThat(result, Matchers.arrayContaining("first"));
    }

    @Test
    public void shouldUseDelegateWithoutAnnotation() throws SecurityException, NoSuchMethodException {
        Mockery mockery = new Mockery();
        final ParameterNameProvider delegate = mockery.mock(ParameterNameProvider.class);
        mockery.checking(new Expectations() {
            {
                one(delegate).parameterNamesFor(Component.class.getMethod("nonAnnotated", String.class));
                will(returnValue(new String[] { "second" }));
            }
        });
        String[] result = new LogicAnnotationWithParanamerParameterNameProvider(delegate)
                .parameterNamesFor(Component.class.getMethod("nonAnnotated", String.class));
        assertThat(result, Matchers.arrayContaining("second"));
    }

}
