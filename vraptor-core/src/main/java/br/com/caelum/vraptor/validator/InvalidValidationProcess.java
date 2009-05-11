package br.com.caelum.vraptor.validator;

import br.com.caelum.vraptor.VRaptorException;

/**
 * Some exception ocurred during the validation process, which makes it an
 * invalid validation process.
 *
 * @author Guilherme Silveira
 */
public class InvalidValidationProcess extends VRaptorException {
    private static final long serialVersionUID = 7058206933201480556L;

    public InvalidValidationProcess(String msg, Throwable e) {
        super(msg, e);
    }

    public InvalidValidationProcess(Throwable e) {
        super(e);
    }

    public InvalidValidationProcess(String msg) {
        super(msg);
    }

}
