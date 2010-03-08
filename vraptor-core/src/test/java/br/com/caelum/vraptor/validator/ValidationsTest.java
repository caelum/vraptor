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

package br.com.caelum.vraptor.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

public class ValidationsTest {

    public static class Client {
        private String name;
        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }
    }

	private ResourceBundle bundle;
	private Validations validations;

	@Before
    public void setup() {
	    this.bundle = ResourceBundle.getBundle("messages");
    	this.validations = new Validations(bundle);
	}

    @Test
    public void canHandleTheSingleCheck() {
        Client guilherme = new Client();
        validations.that(guilherme, notNullValue());
        assertThat(validations.getErrors(), hasSize(0));
    }

    @Test
    public void canHandleTheSingleCheckWhenProblematic() {
        Client guilherme = null;
        validations.that(guilherme, notNullValue());
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void canHandleInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age = 22;
        validations.that(guilherme.getAge(), greaterThan(17));
        assertThat(validations.getErrors(), hasSize(0));
    }

    @SuppressWarnings("null")
	@Test
    public void canIgnoreInternalPrimitiveValidationIfAlreadyNull() {
        final Client guilherme = null;
        if (validations.that(guilherme, notNullValue())) {
            validations.that(guilherme.getAge(), greaterThan(17));
            validations.that(guilherme.getAge(), greaterThanOrEqualTo(12));
        }
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void executesInternalValidationIfSuccessful() {
        final Client guilherme = new Client();
        guilherme.age = 10;
        if (validations.that(guilherme, notNullValue())) {
            validations.that(guilherme.getAge(), greaterThan(17));
            validations.that(guilherme.getAge(), greaterThanOrEqualTo(12));
        }
        assertThat(validations.getErrors(), hasSize(2));
    }

    @Test
    public void complainsAboutInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age = 15;
        validations.that(guilherme.getAge(), greaterThan(17));
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void formatsParameterizedValidationMessagesWhenUsingMatchers() {
        final Client caio = new Client();
        validations.that(caio.getName(), is(notNullValue()), "error", "required_field", "Name");
        assertThat(validations.getErrors(), hasSize(1));
        assertThat(validations.getErrors().get(0).getMessage(), is(equalTo("Name is a required field")));
    }

    @Test
    public void formatsParameterizedValidationMessagesWithSeveralParametersI18ningStringParameters() {
        final Client client = new Client();
        client.setAge(-1);
        validations.that(client.getAge() > 0 && client.getAge() < 100, "error", "between_field",  "Age", 0, 100);
        assertThat(validations.getErrors(), hasSize(1));
        assertThat(validations.getErrors().get(0).getMessage(), is(equalTo("Age should be a value between 0 and 100")));
    }
    @Test
    public void formatsParameterizedValidationMessagesWithI18nedStringParameters() {
    	final Client client = new Client();
    	client.setAge(-1);
    	validations.that(client.getAge() > 0 && client.getAge() < 100, "error", "between_field",  validations.i18n("age"), 0, 100);
    	assertThat(validations.getErrors(), hasSize(1));
    	assertThat(validations.getErrors().get(0).getMessage(), is(equalTo("Age should be a value between 0 and 100")));
    }

}
