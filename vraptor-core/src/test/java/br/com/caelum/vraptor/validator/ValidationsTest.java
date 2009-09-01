/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
        private Long id;
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
        validations.that(guilherme).shouldBe(notNullValue());
        assertThat(validations.getErrors(), hasSize(0));
    }

    @Test
    public void canHandleTheSingleCheckWhenProblematic() {
        Client guilherme = null;
        validations.that(guilherme).shouldBe(notNullValue());
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void canHandleInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age = 22;
        validations.that(guilherme.getAge()).shouldBe(greaterThan(17));
        assertThat(validations.getErrors(), hasSize(0));
    }

    @Test
    public void canIgnoreInternalPrimitiveValidationIfAlreadyNull() {
        final Client guilherme = null;
        validations.that(guilherme).shouldBe(notNullValue()).otherwise(new Validations() {
            @Override
			public void check(){
                that(guilherme.getAge()).shouldBe(greaterThan(17));
                that(guilherme.getAge()).shouldBe(greaterThanOrEqualTo(12));
            }
        });
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void executesInternalValidationIfSuccessful() {
        final Client guilherme = new Client();
        guilherme.age = 10;
        validations.that(guilherme).shouldBe(notNullValue()).otherwise(new Validations() {
            @Override
			public void check(){
                that(guilherme.getAge()).shouldBe(greaterThan(17));
                that(guilherme.getAge()).shouldBe(greaterThanOrEqualTo(12));
            }
        });
        assertThat(validations.getErrors(), hasSize(2));
    }

    @Test
    public void complainsAboutInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age = 15;
        validations.that(guilherme.getAge()).shouldBe(greaterThan(17));
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void formatsParameterizedValidationMessagesWhenUsingMatchers() {
        final Client caio = new Client();
        validations.that("error", "required_field", caio.getName(), is(notNullValue()), "Name");
        assertThat(validations.getErrors(), hasSize(1));
        assertThat(validations.getErrors().get(0).getMessage(), is(equalTo("Name is a required field")));
    }

    @Test
    public void formatsParameterizedValidationMessagesWithSeveralParameters() {
        final Client client = new Client();
        client.setAge(-1);
        validations.that("error", "between_field", client.getAge() > 0 && client.getAge() < 100, "Age", 0, 100);
        assertThat(validations.getErrors(), hasSize(1));
        assertThat(validations.getErrors().get(0).getMessage(), is(equalTo("Age should be a value between 0 and 100")));
    }

}
