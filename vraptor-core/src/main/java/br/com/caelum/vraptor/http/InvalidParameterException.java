
package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.VRaptorException;

/**
 * Some parameters sent by http were invalid.
 *
 * @author Guilherme Silveira
 */
public class InvalidParameterException extends VRaptorException {
    private static final long serialVersionUID = 4632893122633090126L;

    public InvalidParameterException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public InvalidParameterException(Throwable e) {
        super(e);
    }

    public InvalidParameterException(String msg) {
        super(msg);
    }
}
