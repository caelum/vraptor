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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class InfoTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    @Component("customized")
    class CustomComponent {

    }

    @Test
    public void shouldReadAComponentAnnotatedName() {
        assertThat(Info.getComponentName(CustomComponent.class), is(equalTo("customized")));
    }

    @Component
    class DefaultComponents {
        public void noAnnotation() {
        }

        @Logic
        public void emptyAnnotation() {
        }

        @Logic("value")
        public void full() {
        }

    }

    @Test
    public void shouldReadAComponentDefaultName() {
        assertThat(Info.getComponentName(DefaultComponents.class), is(equalTo("DefaultComponents")));
    }

    @Component
    class DefaultController {
    }

    @Test
    public void shouldReadAComponentDefaultNameWithSuffixRemoval() {
        assertThat(Info.getComponentName(DefaultController.class), is(equalTo("default")));
    }

    @Test
    public void shouldDetectAnOldComponent() {
        ResourceClass resource = mockery.resource(CustomComponent.class);
        assertThat(Info.isOldComponent(resource), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    class DefaultResource {

    }

    @Test
    public void shouldDetectANewComponent() {
        ResourceClass resource = mockery.resource(DefaultResource.class);
        assertThat(Info.isOldComponent(resource), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfNoAnnotationPresent() throws SecurityException, NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("noAnnotation")), is(equalTo("noAnnotation")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfAnnotationWithoutValue() throws SecurityException,
            NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("emptyAnnotation")),
                is(equalTo("emptyAnnotation")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfAnnotationWithName() throws SecurityException, NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("full")), is(equalTo("value")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToCapitalizeASingleCharacter() {
        assertThat(Info.capitalize("v"), is(equalTo("V")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToCapitalizeAStringLongerThanOneCharacter() {
        assertThat(Info.capitalize("value"), is(equalTo("Value")));
        mockery.assertIsSatisfied();
    }

}
