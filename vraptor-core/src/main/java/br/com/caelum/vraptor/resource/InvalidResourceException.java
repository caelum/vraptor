
package br.com.caelum.vraptor.resource;

import br.com.caelum.vraptor.VRaptorException;

/**
 * An invalid resource was loaded.
 * @author guilherme silveira
 *
 */
public class InvalidResourceException extends VRaptorException {
    private static final long serialVersionUID = -981845067568509810L;

    public InvalidResourceException(Throwable e) {
        super(e);
    }

    public InvalidResourceException(String msg, Throwable e) {
        super(msg, e);
    }

    public InvalidResourceException(String msg) {
		super(msg);
	}

}
