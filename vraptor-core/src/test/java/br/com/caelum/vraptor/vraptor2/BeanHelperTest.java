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

import org.junit.Test;

public class BeanHelperTest {

    interface CustomType {
        boolean isValid();
        String getValue();
    }
    @Test
    public void canHandlePrimitiveBooleanGetter() throws SecurityException, NoSuchMethodException {
        assertThat(new BeanHelper().nameForGetter(CustomType.class.getMethod("isValid")), is(equalTo("valid")));
    }
    
    @Test
    public void canHandleCustomTypeGetter() throws SecurityException, NoSuchMethodException {
        assertThat(new BeanHelper().nameForGetter(CustomType.class.getMethod("getValue")), is(equalTo("value")));
    }
    
    @Test
    public void canDecapitalizeASingleCharacter() {
        assertThat(new BeanHelper().decapitalize("A"), is(equalTo("a")));
    }
    
}
