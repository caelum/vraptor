package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

public class ValidatorAcceptanceTest {

    class Student {
        private Long id;
    }

    @Test(expected = ValidationError.class)
    public void cheksThatValidationWorks() {
        DefaultValidator validator = new DefaultValidator();
        final Student guilherme = new Student();
        validator.checking(new Validations() {
            {
                that(guilherme.id, is(notNullValue()));
            }
        });
    }

    @Test
    public void validDataDoesntThrowException() {
        DefaultValidator validator = new DefaultValidator();
        final Student guilherme = new Student();
        guilherme.id = 15L;
        validator.checking(new Validations() {
            {
                // this is the Assertion itself
                that(guilherme.id, is(notNullValue()));
            }
        });
    }

}
