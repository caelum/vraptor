package br.com.caelum.vraptor.validator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ValidationsTest {

    public static class Client {
        private Long id;
        private String name;
        private int age;
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
        Client guilherme = new Client();
        Validations validations = new Validations();
        validations.that(guilherme);
        validations.shouldBe(notNullValue());
        assertThat(validations.getErrors(), hasSize(1));
    }

}
