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

import java.util.List;
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
    public void shouldUseTheConstructorResourceBundle() {
    	Validations validations = new Validations(singletonBundle("some.message", "The value"));

    	validations.that(false, "category", "some.message");

    	assertThat(validations.getErrors().get(0).getMessage(), is("The value"));
    }

    @Test
    public void shouldUseTheConstructorResourceBundleFirst() {
    	Validations validations = new Validations(singletonBundle("some.message", "The value"));

    	validations.that(false, "category", "some.message");

    	List<Message> errors = validations.getErrors(singletonBundle("some.message", "Other value"));

		assertThat(errors.get(0).getMessage(), is("The value"));
    }

    @Test
    public void shouldFallbackToGivenResourceBundle() {
    	Validations validations = new Validations(singletonBundle("some.message", "The value"));

    	validations.that(false, "category", "some.other.message");

    	List<Message> errors = validations.getErrors(singletonBundle("some.other.message", "Other value"));

    	assertThat(errors.get(0).getMessage(), is("Other value"));
    }

    @Test
    public void shouldFallbackToDefaultMessage() {
    	Validations validations = new Validations(singletonBundle("some.message", "The value"));

    	validations.that(false, "category", "a.different.message");

    	List<Message> errors = validations.getErrors(singletonBundle("some.other.message", "Other value"));

    	assertThat(errors.get(0).getMessage(), is("???a.different.message???"));
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

    @Test
    public void should18nalizeParametersUsingConstructorBundle() {
    	Validations validations = new Validations(singletonBundle("some.message", "The value")) {{
            that(false, "category", "some.param.message", i18n("some.message"));
        }};

    	List<Message> errors = validations.getErrors(singletonBundle("some.param.message", "The param {0} sucks"));

    	assertThat(errors.get(0).getMessage(), is("The param The value sucks"));
    }

    @Test
    public void should18nalizeParametersUsingGivenBundle() {
    	Validations validations = new Validations(singletonBundle("some.param.message", "The param {0} sucks")) {{
            that(false, "category", "some.param.message", i18n("some.message"));
        }};

    	List<Message> errors = validations.getErrors(singletonBundle("some.message", "The value"));

    	assertThat(errors.get(0).getMessage(), is("The param The value sucks"));
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

    @Test
    public void should18nalizeTheCategoryParameterUsingGivenBundle() {
    	Validations validations = new Validations() {{
    		that(false, i18n("some.category"), "some.message");
    	}};

    	List<Message> errors = validations.getErrors(singletonBundle("some.category", "The Category"));

    	assertThat(errors.get(0).getCategory(), is("The Category"));
    }

    @Test
    public void should18nalizeTheCategoryParameterUsingMatchersWithReasonGivenBundle() {
    	Validations validations = new Validations() {{
    		that(null, is(notNullValue()), i18n("some.category"), "some.reason");
    	}};

    	List<Message> errors = validations.getErrors(singletonBundle("some.category", "The Category"));

    	assertThat(errors.get(0).getCategory(), is("The Category"));
    }

    @Test
    public void should18nalizeTheCategoryParameterUsingMatchersWithoutReasonGivenBundle() {
    	Validations validations = new Validations() {{
    		that(null, is(notNullValue()), i18n("some.category"));
    	}};

    	List<Message> errors = validations.getErrors(singletonBundle("some.category", "The Category"));

    	assertThat(errors.get(0).getCategory(), is("The Category"));
    }

    private ResourceBundle singletonBundle(final String key, final String value) {
		return new SingletonResourceBundle(key, value);
	}

}
