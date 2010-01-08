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
package br.com.caelum.vraptor.mydvds.validation;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This class creates custom Hamcrest matchers for making validations more readable.
 * @author Lucas Cavalcanti
 *
 */
public class CustomMatchers {

    private static TypeSafeMatcher<String> EMPTY = new TypeSafeMatcher<String>() {

        @Override
        protected void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText(" " + item);
        }

        @Override
        protected boolean matchesSafely(String item) {
            return item != null && !item.equals("");
        }

        public void describeTo(Description description) {
            description.appendText(" not empty");
        }

    };

    /**
     * matches if the given string is not empty. This matcher is null-safe.
     * @return
     */
    public static TypeSafeMatcher<String> notEmpty() {
        return EMPTY;
    }

}
