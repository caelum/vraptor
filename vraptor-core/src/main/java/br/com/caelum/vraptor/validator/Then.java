package br.com.caelum.vraptor.validator;

public class Then<T> {

    private final Validations actual;

    public Then(Validations actual) {
        this.actual = actual;
    }

    public void then(Validations validations) {
        validations.check();
        actual.and(validations.getErrors());
    }

}
