package br.com.caelum.vraptor.validator;

/**
 * Some exception ocurred during the validation process, which makes it an
 * invalid validation process.
 * 
 * @author Guilherme Silveira
 */
public class InvalidValidationProcess extends RuntimeException {

    public InvalidValidationProcess(Throwable e) {
        super(e);
    }

}
