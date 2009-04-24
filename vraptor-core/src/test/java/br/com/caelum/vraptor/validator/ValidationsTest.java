package br.com.caelum.vraptor.validator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ValidationsTest {

    public static class Client {
        private Long id;
        private String name;
        private int age;
        public int getAge() {
            return age;
        }
    }

    @Test
    public void canHandleTheSingleCheck() {
        Client guilherme = new Client();
        Validations validations = new Validations();
        validations.that(guilherme);
        validations.shouldBe(notNullValue());
    }



    @Test
    public void canHandleTheSingleCheckWhenProblematic() {
        Client guilherme = null;
        Validations validations = new Validations();
        validations.that(guilherme);
        validations.shouldBe(notNullValue());
        assertThat(validations.getErrors(), hasSize(1));
    }

    @Test
    public void canHandleInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age=22;
        Validations validations = new Validations();
        validations.that(guilherme).getAge();
        validations.shouldBe(greaterThan(17));
    }

    @Test
    public void canIgnoreInternalPrimitiveValidationIfAlreadyNull() {
        Client guilherme = null;
        Validations validations = new Validations();
        validations.that(guilherme).getAge();
        validations.shouldBe(greaterThan(17));
    }
    @Test
    public void complainsAboutInternalPrimitiveValidation() {
        Client guilherme = new Client();
        guilherme.age=15;
        Validations validations = new Validations();
        validations.that(guilherme).getAge();
        validations.shouldBe(greaterThan(17));
        assertThat(validations.getErrors(), hasSize(1));
    }

}
