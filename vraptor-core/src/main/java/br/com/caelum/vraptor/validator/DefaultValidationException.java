package br.com.caelum.vraptor.validator;

/**
 * VRaptor's default implementation of a {@link ValidationException}.
 * Users can use this class if they don't want to implement their own business exceptions 
 * 
 * @author Sérgio Lopes
 * @see ValidationException
 */
@ValidationException
public class DefaultValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DefaultValidationException(String msg) {
		super(msg);
	}
	
	public DefaultValidationException(Throwable cause) {
		super(cause);
	}
	
	public DefaultValidationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
