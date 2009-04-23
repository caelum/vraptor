package br.com.caelum.vraptor.validator;

import org.junit.Test;

public class ValidationsTest {

    public static class Client {
        private Long id;
        private String name;
        private int age;
    }

    @Test
    public void canHandleTheSingleCheck() {
        Validations validations = new Validations();
        
    }

}
