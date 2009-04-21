package br.com.caelum.vraptor.validator;


public class InvalidValidationProcess extends RuntimeException {

    public InvalidValidationProcess(Throwable e) {
        super(e);
    }

}
